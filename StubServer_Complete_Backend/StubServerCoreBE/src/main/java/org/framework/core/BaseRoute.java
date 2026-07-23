package org.framework.core;

import org.framework.core.expansion.TemplateReplacer;
import org.framework.core.impl.GroovyScriptExecutor;
import org.framework.core.impl.MockResponseProcessor;
import org.framework.core.impl.PropertyResolver;
import org.framework.properties.MockResponse;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.utils.JsonXmlValueExtractor;
import org.framework.utils.RequestParser;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// dispatch style
// mock responses
public abstract class BaseRoute {
    ResponseGenerator generator;
    List<MockResponse> responses;
    String contentType;
    private String name;
    private String dispatchStyle;
    String path;
    private String serviceName;

    String defaultResponse;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDispatchStyle() {
        return dispatchStyle;
    }

    public void setDispatchStyle(String dispatchStyle) {
        this.dispatchStyle = dispatchStyle;
    }

    public BaseRoute(String serviceName, ResponseGenerator generator, List<MockResponse> responses,
            String defaultResponse, String name, String dispatchStyle, String contentType) {
        this.generator = generator;
        this.responses = responses;
        this.defaultResponse = defaultResponse;
        this.name = name;
        this.dispatchStyle = dispatchStyle;
        this.contentType = contentType;
        this.serviceName = serviceName;
    }

    public MockResponse getResponse(Context context, MockRequest request) throws Exception {
        context.setMockOperation(this);
        MockResponse resp = getMockResponse(context, request);
        return processResponse(context, resp, request);
    }

    public MockResponse getMockResponse(Context context, MockRequest request) throws UnsupportedEncodingException {
        String response = generator.GenerateResponse(context, request);
        // System.out.println("Mock Service properties
        // "+context.mockService.getMockServiceProperties());
        if (response == null) {
            return getDefaultResponse();
        }

        if (response.equals("LIVE")) {
            MockResponse resp = new MockResponse(this.serviceName, "LIVE");
            return resp;
        }

        for (MockResponse resp : responses) {
            if (resp.getName().equals(response)) {
                return resp;
            }
        }

        return getDefaultResponse();
    }

    public MockResponse processResponse(Context context, MockResponse resp, MockRequest request) throws Exception {

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

    private String resolvePlaceholders(String responseBody, Context context, MockRequest request) throws Exception {
        TemplateReplacer replacer = new TemplateReplacer();
        // System.out.println("property
        // :"+context.mockService.getPropertyValue("account"));
        if (responseBody.startsWith("<")) {
            contentType = "Xml";
        } else {
            contentType = "Json";
        }
        System.out.println("content type " + contentType);
        if (responseBody.contains("#MockResponse#Request"))
            storeMagicStringProperties(responseBody, contentType, context, request);
        replacer.addPropertyMap("MockService", context.getMockService().getMockServiceProperties());
        replacer.addPropertyMap("", context.getDynamicProperties());
        // for magic string add the properties and its value in context map

        return replacer.replaceTemplate(responseBody);
    }

    void storeMagicStringProperties(String responseBody, String contentType, Context context, MockRequest request)
            throws Exception {
        List<Object> valueList = null;
        if (contentType.equals("Json"))
            valueList = JsonXmlValueExtractor.extractValuesFromJson(responseBody);
        if (contentType.equals("Xml"))
            valueList = JsonXmlValueExtractor.extractValuesFromXml(responseBody);
        RequestParser parser = new RequestParser();
        for (Object property : valueList) {
            String propertyString = property.toString();
            Object value = parser.getRequestValue(request.getRequestContent(), propertyString);
            context.setProperty(propertyString.substring(2, propertyString.indexOf("}")), value);
            // System.out.println("Value "+value);
        }

    }

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
        return sb.toString(); // Do not re-escape quotes
    }

    public List<MockResponse> getResponses() {
        return responses;
    }

    public MockResponse getDefaultResponse() {
        for (MockResponse response : responses) {
            if (response.getName().equals(defaultResponse)) {
                return response;
            }
        }
        return responses.getFirst();
    }

    public abstract boolean matchRequest(Context context, MockRequest request);
}
