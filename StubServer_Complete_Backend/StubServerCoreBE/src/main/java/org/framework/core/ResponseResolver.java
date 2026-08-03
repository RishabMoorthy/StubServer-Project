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
 *
 * <p>
 * An Endpoint/StubOperation can declare several RRPairs. They are evaluated in
 * XML document order on every request - the CA DevTest-style chain the new
 * contract expects - and the first pair that produces a response wins:
 * a Script pair "produces a response" when its MatchScript returns a name; an
 * Operation pair (or any non-Script style) produces one when its own
 * {@code vs:Request} criteria match the live request. A pair that declines
 * falls through to the next one. If none match, {@code defaultRR} names the
 * pair to fall back to, preserving old single-pair behaviour for the common
 * case of one RRPair per endpoint/operation.
 */
public class ResponseResolver {

    /** Runtime state for a single RRPair within an Endpoint/StubOperation. */
    private static final class PairRuntime {
        RRPair pair;
        ResponseGenerator scriptGenerator; // non-null only for MatchStyle=Script
        List<MockResponse> responses;
        String defaultResponseName;
    }

    /** Runtime state for one Endpoint or StubOperation: its RRPair chain. */
    private static final class OperationRuntime {
        List<PairRuntime> pairs = new ArrayList<>();
        String defaultRR;
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
        runtime.defaultRR = endpoint.getDefaultRR();
        runtime.contentType = "";

        for (RRPair pair : endpoint.getRrList()) {
            runtime.pairs.add(buildPairRuntime(pair, restResponses(pair, endpoint.getPath())));
        }

        runtimes.put(endpoint, runtime);
    }

    public void register(StubOperation operation) {
        OperationRuntime runtime = new OperationRuntime();
        runtime.defaultRR = operation.getDefaultRR();
        runtime.contentType = "Xml";

        for (RRPair pair : operation.getRrList()) {
            runtime.pairs.add(buildPairRuntime(pair, soapResponses(pair)));
        }

        runtimes.put(operation, runtime);
    }

    private PairRuntime buildPairRuntime(RRPair pair, List<MockResponse> responses) {
        PairRuntime runtime = new PairRuntime();
        runtime.pair = pair;
        runtime.responses = responses;
        runtime.defaultResponseName = defaultResponseName(pair, responses);
        runtime.scriptGenerator = isScriptStyle(pair) ? generatorFor(pair) : null;
        return runtime;
    }

    private boolean isScriptStyle(RRPair pair) {
        return pair.getResponseSelection() != null
                && "Script".equalsIgnoreCase(trimToEmpty(pair.getResponseSelection().getMatchStyle()));
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

    /**
     * Was BaseRoute.getMockResponse(). Walks the operation's RRPair chain in
     * document order; the first pair that matches wins.
     */
    public MockResponse getMockResponse(Object operation, Context context, MockRequest request)
            throws UnsupportedEncodingException {

        OperationRuntime runtime = runtimes.get(operation);
        if (runtime == null) {
            return null;
        }

        for (PairRuntime pair : runtime.pairs) {
            if (pair.scriptGenerator != null) {
                MockResponse resp = resolveScriptPair(pair, context, request);
                if (resp != null) {
                    System.out.println("[RRPairChain] pair=" + pair.pair.getId() + " style=Script matched -> " + resp.getName());
                    return resp;
                }
                System.out.println("[RRPairChain] pair=" + pair.pair.getId() + " style=Script declined, trying next");
                continue;
            }

            if (RRPairRequestMatcher.matches(pair.pair.getRequest(), context, request)) {
                MockResponse resp = findByName(pair.responses, pair.defaultResponseName);
                if (resp != null) {
                    System.out.println("[RRPairChain] pair=" + pair.pair.getId() + " style=Operation matched -> " + resp.getName());
                    return resp;
                }
            }
            System.out.println("[RRPairChain] pair=" + pair.pair.getId() + " style=Operation declined, trying next");
        }

        MockResponse fallback = fallbackResponse(runtime);
        System.out.println("[RRPairChain] no pair matched, falling back to defaultRR=" + runtime.defaultRR
                + " -> " + (fallback == null ? "null" : fallback.getName()));
        return fallback;
    }

    /**
     * Runs a Script-style pair's MatchScript. A null result means the pair
     * declined - the caller moves on to the next pair in the chain. "LIVE" is
     * a script-level escape hatch to force live invocation for this request.
     */
    private MockResponse resolveScriptPair(PairRuntime pair, Context context, MockRequest request) {
        String responseName = pair.scriptGenerator.GenerateResponse(context, request);
        if (responseName == null) {
            return null;
        }
        if (responseName.equals("LIVE")) {
            return new MockResponse(this.serviceName, "LIVE");
        }

        MockResponse resp = findByName(pair.responses, responseName);
        if (resp != null) {
            return resp;
        }
        // Script named a response that doesn't exist in its own ResponseSet -
        // treat as "matched, use this pair's default" rather than failing the
        // whole chain over a typo.
        return findByName(pair.responses, pair.defaultResponseName);
    }

    private MockResponse findByName(List<MockResponse> responses, String name) {
        if (name == null) {
            return null;
        }
        for (MockResponse resp : responses) {
            if (resp.getName().equals(name)) {
                return resp;
            }
        }
        return null;
    }

    /**
     * Same shape as the "no endpoint/operation matched" 404 in
     * MockRequestHandler/SoapRequestHandler, for the case where an
     * endpoint/operation matched but its RRPair chain had nothing to serve.
     */
    private MockResponse noResponseConfigured() {
        Headers headers = new Headers();
        headers.add("content-type", "plain/text");
        String out = "No response is configured for this request";
        return new MockResponse("No Response Configured", headers, 404,
                out.getBytes(StandardCharsets.UTF_8), "", "", serviceName);
    }

    /** No pair in the chain matched - fall back to the designated defaultRR pair. */
    private MockResponse fallbackResponse(OperationRuntime runtime) {
        for (PairRuntime pair : runtime.pairs) {
            if (pair.pair.getId() != null && pair.pair.getId().equals(runtime.defaultRR)) {
                MockResponse resp = findByName(pair.responses, pair.defaultResponseName);
                if (resp != null) {
                    return resp;
                }
            }
        }
        for (PairRuntime pair : runtime.pairs) {
            MockResponse resp = findByName(pair.responses, pair.defaultResponseName);
            if (resp != null) {
                return resp;
            }
        }
        return null;
    }

    /** Was BaseRoute.processResponse(). */
    public MockResponse processResponse(Object operation, Context context, MockResponse resp, MockRequest request)
            throws Exception {

        // getMockResponse() returns null when the endpoint/operation matched but its
        // RRPair chain produced nothing usable (no pair matched, and none had a
        // fallback default). Without this, LiveInvocation.getLiveResponse() and the
        // plain-mock path below both dereference resp directly and NPE instead of
        // giving the caller a real HTTP response.
        if (resp == null) {
            resp = noResponseConfigured();
        }

        if (context.getMockService().getConfig().isRouteModeEnabled()) {
            LiveInvocation liveinvocation = new LiveInvocation();
            return liveinvocation.getLiveResponse(context, request, resp);
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

    /** Aggregates responses across every RRPair in the operation's chain. */
    public List<MockResponse> getResponses(Object operation) {
        OperationRuntime runtime = runtimes.get(operation);
        if (runtime == null) {
            return new ArrayList<>();
        }
        List<MockResponse> all = new ArrayList<>();
        for (PairRuntime pair : runtime.pairs) {
            all.addAll(pair.responses);
        }
        return all;
    }

    public MockResponse getMockResponseByName(Object operation, String mockResponseName) {
        for (MockResponse response : getResponses(operation)) {
            if (response.getName().equals(mockResponseName)) {
                return response;
            }
        }
        return null;
    }

    /** SCRIPT if any RRPair in the chain is Script-style, SEQUENCE otherwise. */
    public String getDispatchStyle(Object operation) {
        OperationRuntime runtime = runtimes.get(operation);
        if (runtime == null) {
            return "SEQUENCE";
        }
        for (PairRuntime pair : runtime.pairs) {
            if (pair.scriptGenerator != null) {
                return "SCRIPT";
            }
        }
        return "SEQUENCE";
    }

    // ---- building runtime state from the com.stubio model ----

    private ResponseGenerator generatorFor(RRPair pair) {
        String script = pair.getResponseSelection().getMatchScript() == null
                ? ""
                : trimToEmpty(pair.getResponseSelection().getMatchScript().getScript());
        return new GroovyScriptResponseGenerator(script);
    }

    private String defaultResponseName(RRPair pair, List<MockResponse> responses) {
        if (pair != null && pair.getDefaultResponse() != null && !pair.getDefaultResponse().isBlank()) {
            return pair.getDefaultResponse();
        }
        return responses.isEmpty() ? null : responses.get(0).getName();
    }

    /**
     * REST responses carry headers, the endpoint path and the service name so
     * that runtime edits from Groovy can be written back.
     */
    private List<MockResponse> restResponses(RRPair pair, String path) {
        List<MockResponse> mockResponses = new ArrayList<>();
        if (pair.getResponseSet() == null) {
            return mockResponses;
        }
        for (Response response : pair.getResponseSet()) {
            mockResponses.add(new MockResponse(
                    trimToEmpty(response.getName()),
                    responseHeaders(response),
                    response.getStatusCode(),
                    bodyBytes(response),
                    scriptOf(response.getResponseScript()),
                    path,
                    serviceName));
        }
        return mockResponses;
    }

    /**
     * SOAP keeps the header-less construction the old SoapXmlParser used, so
     * SoapService's own content-type defaulting is unaffected.
     */
    private List<MockResponse> soapResponses(RRPair pair) {
        List<MockResponse> mockResponses = new ArrayList<>();
        if (pair.getResponseSet() == null) {
            return mockResponses;
        }
        for (Response response : pair.getResponseSet()) {
            mockResponses.add(new MockResponse(
                    trimToEmpty(response.getName()),
                    response.getStatusCode(),
                    bodyBytes(response),
                    scriptOf(response.getResponseScript())));
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
