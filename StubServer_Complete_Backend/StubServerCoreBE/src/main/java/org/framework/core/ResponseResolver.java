package org.framework.core;

import com.stubio.util.Endpoint;
import com.stubio.util.GenericProperty;
import com.stubio.util.RRPair;
import com.stubio.util.Response;
import com.stubio.util.Script;
import com.stubio.util.StubOperation;

import com.sun.net.httpserver.Headers;

import org.framework.core.expansion.TemplateReplacer;
import org.framework.core.impl.GroovyScriptExecutor;
import org.framework.core.impl.GroovyScriptResponseGenerator;
import org.framework.core.impl.SequenceResponseGenerator;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.properties.MockResponse;
import org.framework.utils.JsonXmlValueExtractor;
import org.framework.utils.RequestParser;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Response selection and post-processing for the com.stubio model.
 *
 * <p>
 * This holds the logic that used to live inside BaseRoute / RestRoute /
 * SoapRoute. The new model classes ({@link Endpoint}, {@link StubOperation})
 * are plain data holders, so the runtime state that a route used to carry -
 * the response generator, the MockResponse instances and the default response
 * name - is built once here at deploy time and keyed by the operation itself.
 *
 * <p>
 * Building the MockResponse list once (rather than per request) is deliberate:
 * Groovy scripts mutate the live MockResponse via setResponseContent() /
 * setBinaryData() and expect those edits to persist, exactly as they did when
 * BaseRoute owned the list.
 */
public class ResponseResolver {

    /** Runtime state for one Endpoint or StubOperation. */
    private static final class OperationRuntime {
        ResponseGenerator generator;
        List<MockResponse> responses;
        String defaultResponse;
        String contentType;
    }

    private final Map<Object, OperationRuntime> runtimes = new IdentityHashMap<>();
    private final String serviceName;

    public ResponseResolver(String serviceName) {
        this.serviceName = serviceName;
    }

    // ---- registration (deploy time) ----

    public void register(Endpoint endpoint) {
        OperationRuntime runtime = new OperationRuntime();
        RRPair primary = primaryPair(endpoint.getRrList(), endpoint.getDefaultRR());

        runtime.generator = generatorFor(primary);
        runtime.responses = restResponses(endpoint);
        runtime.defaultResponse = defaultResponseName(primary, runtime.responses);
        runtime.contentType = "";

        runtimes.put(endpoint, runtime);
    }

    public void register(StubOperation operation) {
        OperationRuntime runtime = new OperationRuntime();
        RRPair primary = primaryPair(operation.getRrList(), operation.getDefaultRR());

        runtime.generator = generatorFor(primary);
        runtime.responses = soapResponses(operation);
        runtime.defaultResponse = defaultResponseName(primary, runtime.responses);
        runtime.contentType = "Xml";

        runtimes.put(operation, runtime);
    }

    // ---- resolution (request time) ----

    /**
     * Was BaseRoute.getResponse(). {@code operation} is an Endpoint or a
     * StubOperation.
     */
    public MockResponse getResponse(Object operation, Context context, MockRequest request) throws Exception {
        context.setMockOperation(operation);
        MockResponse resp = getMockResponse(operation, context, request);
        return processResponse(operation, context, resp, request);
    }

    /** Was BaseRoute.getMockResponse(). */
    public MockResponse getMockResponse(Object operation, Context context, MockRequest request)
            throws UnsupportedEncodingException {

        OperationRuntime runtime = runtimes.get(operation);
        if (runtime == null) {
            return null;
        }

        String response = runtime.generator.GenerateResponse(context, request);
        if (response == null) {
            return getDefaultResponse(runtime);
        }

        if (response.equals("LIVE")) {
            return new MockResponse(this.serviceName, "LIVE");
        }

        for (MockResponse resp : runtime.responses) {
            if (resp.getName().equals(response)) {
                return resp;
            }
        }

        return getDefaultResponse(runtime);
    }

    /** Was BaseRoute.getDefaultResponse(). */
    private MockResponse getDefaultResponse(OperationRuntime runtime) {
        for (MockResponse response : runtime.responses) {
            if (response.getName().equals(runtime.defaultResponse)) {
                return response;
            }
        }
        return runtime.responses.isEmpty() ? null : runtime.responses.get(0);
    }

    /** Was BaseRoute.processResponse(). */
    public MockResponse processResponse(Object operation, Context context, MockResponse resp, MockRequest request)
            throws Exception {

        if (context.getMockService().getConfig().isRouteModeEnabled()) {
            LiveInvocation liveinvocation = new LiveInvocation();
            return liveinvocation.getLiveResponse(context, request, resp);
        }
        if (resp == null) {
            return resp;
        }

        if (resp.getScript() != null && !resp.getScript().isEmpty()) {
            GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
            HashMap<String, Object> configureResponseScriptProperties = new HashMap<>();
            configureResponseScriptProperties.put("context", context);
            configureResponseScriptProperties.put("mockResponse", resp);
            groovyExecutor.executeScript(resp.getScript(), configureResponseScriptProperties, context);
        }

        String resolvedBody = resolveDateExpressions(new String(resp.getResponseBytes(), "UTF-8"));
        resolvedBody = resolvePlaceholders(resolvedBody, context, request);
        resp = resp.clone();
        resp.setResponseBytes(resolvedBody.getBytes(StandardCharsets.UTF_8));
        return resp;
    }

    /** Was BaseRoute.resolvePlaceholders(). */
    private String resolvePlaceholders(String responseBody, Context context, MockRequest request) throws Exception {
        TemplateReplacer replacer = new TemplateReplacer();
        String contentType;
        if (responseBody.startsWith("<")) {
            contentType = "Xml";
        } else {
            contentType = "Json";
        }
        System.out.println("content type " + contentType);
        if (responseBody.contains("#MockResponse#Request")) {
            storeMagicStringProperties(responseBody, contentType, context, request);
        }
        replacer.addPropertyMap("MockService", context.getMockService().getMockServiceProperties());
        replacer.addPropertyMap("", context.getDynamicProperties());

        return replacer.replaceTemplate(responseBody);
    }

    /** Was BaseRoute.storeMagicStringProperties(). */
    private void storeMagicStringProperties(String responseBody, String contentType, Context context,
            MockRequest request) throws Exception {
        List<Object> valueList = null;
        if (contentType.equals("Json")) {
            valueList = JsonXmlValueExtractor.extractValuesFromJson(responseBody);
        }
        if (contentType.equals("Xml")) {
            valueList = JsonXmlValueExtractor.extractValuesFromXml(responseBody);
        }
        RequestParser parser = new RequestParser();
        for (Object property : valueList) {
            String propertyString = property.toString();
            Object value = parser.getRequestValue(request.getRequestContent(), propertyString);
            context.setProperty(propertyString.substring(2, propertyString.indexOf("}")), value);
        }
    }

    /** Was BaseRoute.resolveDateExpressions(). */
    public static String resolveDateExpressions(String json) {
        Pattern pattern = Pattern.compile(
                "\\$\\{=new java\\.text\\.SimpleDateFormat\\(\\\"([^\\\"]+)\\\"\\)\\.format\\(new java\\.util\\.Date\\(\\)\\)\\}");
        Matcher matcher = pattern.matcher(json);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String format = matcher.group(1);
            try {
                String formattedDate = new SimpleDateFormat(format).format(new Date());
                matcher.appendReplacement(sb, Matcher.quoteReplacement(formattedDate));
            } catch (Exception e) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement("ERROR_DATE_FORMAT"));
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    // ---- accessors kept for parity with the old route API ----

    public List<MockResponse> getResponses(Object operation) {
        OperationRuntime runtime = runtimes.get(operation);
        return runtime == null ? new ArrayList<>() : runtime.responses;
    }

    public MockResponse getMockResponseByName(Object operation, String mockResponseName) {
        for (MockResponse response : getResponses(operation)) {
            if (response.getName().equals(mockResponseName)) {
                return response;
            }
        }
        return null;
    }

    public String getDispatchStyle(Object operation) {
        OperationRuntime runtime = runtimes.get(operation);
        if (runtime == null) {
            return "SEQUENCE";
        }
        return runtime.generator instanceof GroovyScriptResponseGenerator ? "SCRIPT" : "SEQUENCE";
    }

    // ---- building runtime state from the com.stubio model ----

    /**
     * The old format declared one dispatch style per operation; in the new model
     * it lives on the RRPair. The pair referenced by defaultRR drives the
     * operation, falling back to the first pair.
     */
    private RRPair primaryPair(List<RRPair> pairs, String defaultRR) {
        if (pairs == null || pairs.isEmpty()) {
            return null;
        }
        if (defaultRR != null && !defaultRR.isBlank()) {
            for (RRPair pair : pairs) {
                if (defaultRR.equals(pair.getId())) {
                    return pair;
                }
            }
        }
        return pairs.get(0);
    }

    private ResponseGenerator generatorFor(RRPair pair) {
        boolean scriptStyle = pair != null
                && pair.getResponseSelection() != null
                && "Script".equalsIgnoreCase(trimToEmpty(pair.getResponseSelection().getMatchStyle()));

        if (scriptStyle) {
            String script = pair.getResponseSelection().getMatchScript() == null
                    ? ""
                    : trimToEmpty(pair.getResponseSelection().getMatchScript().getScript());
            return new GroovyScriptResponseGenerator(script);
        }

        return new SequenceResponseGenerator();
    }

    private String defaultResponseName(RRPair pair, List<MockResponse> responses) {
        if (pair != null && pair.getDefaultResponse() != null && !pair.getDefaultResponse().isBlank()) {
            return pair.getDefaultResponse();
        }
        return responses.isEmpty() ? null : responses.get(0).getName();
    }

    /**
     * REST responses carry headers, the operation path and the service name so
     * that runtime edits from Groovy can be written back.
     */
    private List<MockResponse> restResponses(Endpoint endpoint) {
        List<MockResponse> mockResponses = new ArrayList<>();
        for (RRPair pair : endpoint.getRrList()) {
            if (pair.getResponseSet() == null) {
                continue;
            }
            for (Response response : pair.getResponseSet()) {
                mockResponses.add(new MockResponse(
                        trimToEmpty(response.getName()),
                        responseHeaders(response),
                        response.getStatusCode(),
                        bodyBytes(response),
                        scriptOf(response.getResponseScript()),
                        endpoint.getPath(),
                        serviceName));
            }
        }
        return mockResponses;
    }

    /**
     * SOAP keeps the header-less construction the old SoapXmlParser used, so
     * SoapService's own content-type defaulting is unaffected.
     */
    private List<MockResponse> soapResponses(StubOperation operation) {
        List<MockResponse> mockResponses = new ArrayList<>();
        for (RRPair pair : operation.getRrList()) {
            if (pair.getResponseSet() == null) {
                continue;
            }
            for (Response response : pair.getResponseSet()) {
                mockResponses.add(new MockResponse(
                        trimToEmpty(response.getName()),
                        response.getStatusCode(),
                        bodyBytes(response),
                        scriptOf(response.getResponseScript())));
            }
        }
        return mockResponses;
    }

    private Headers responseHeaders(Response response) {
        Headers headers = new Headers();

        String contentType = trimToEmpty(response.getContentType());
        if (contentType.isEmpty()) {
            contentType = "application/json";
        }
        if (!contentType.toLowerCase().contains("charset")) {
            contentType += "; charset=UTF-8";
        }
        headers.add("Content-Type", contentType);

        if (response.getCustomHeaders() != null) {
            for (GenericProperty header : response.getCustomHeaders()) {
                if (header.getKey() != null && !header.getKey().isBlank()) {
                    headers.add(header.getKey(), trimToEmpty(header.getValue()));
                }
            }
        }

        return headers;
    }

    private byte[] bodyBytes(Response response) {
        return trimToEmpty(response.getBody()).getBytes(StandardCharsets.UTF_8);
    }

    private String scriptOf(Script script) {
        return script == null ? "" : trimToEmpty(script.getScript());
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
