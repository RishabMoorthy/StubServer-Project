package org.framework.config;

import com.sun.net.httpserver.Headers;
import org.framework.core.BaseRoute;
import org.framework.core.ParsedXMLObject;
import org.framework.core.ResponseGenerator;
import org.framework.core.impl.GroovyScriptResponseGenerator;
import org.framework.core.impl.SequenceResponseGenerator;
import org.framework.datasource.DataSourceDefinition;
import org.framework.datasource.DataSourceRegistry;
import org.framework.datasource.DataSourceType;
import org.framework.properties.MockResponse;
import org.framework.services.rest.RestRoute;
import org.framework.utils.Logger;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class RestXmlParser extends Parser {

    public ParsedXMLObject parseRestRoutes(File xmlFile) throws Exception {
        ParsedXMLObject parsedObj = new ParsedXMLObject();
        String serviceName = null;
        try {
            List<BaseRoute> routes = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // XPath to find dataSource with type Excel
            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = "//*[local-name()='dataSource' and @type='Excel']" +
                    " | " +
                    "//*[local-name()='File']/*[local-name()='FileType']";

            XPathExpression xPathExpr = xpath.compile(expression);

            Object result = xPathExpr.evaluate(document, XPathConstants.NODE);
            DataSourceRegistry registry = null;
            boolean datasourceEnabled = false;

            NodeList restMockServiceNodes = document.getElementsByTagName("con:restMockService");
            Element restMockService = (Element) restMockServiceNodes.item(0);
            int delay = 0;
            String startScript = null, stopScript = null, onRequestScript = null, afterRequestScript = null;
            NodeList startScriptNodes = restMockService.getElementsByTagName("con:startScript");
            if (startScriptNodes.getLength() > 0) {
                if (startScriptNodes.item(0) != null)
                    startScript = startScriptNodes.item(0).getTextContent().trim();
            }

            NodeList stopScriptNodes = restMockService.getElementsByTagName("con:stopScript");
            if (stopScriptNodes.getLength() > 0) {
                if (stopScriptNodes.item(0) != null)
                    stopScript = stopScriptNodes.item(0).getTextContent().trim();
            }

            NodeList onRequestScriptNodes = restMockService.getElementsByTagName("con:onRequestScript");
            if (onRequestScriptNodes.getLength() > 0) {
                if (onRequestScriptNodes.item(0) != null)
                    onRequestScript = onRequestScriptNodes.item(0).getTextContent().trim();
            }

            NodeList afterRequestScriptNodes = restMockService.getElementsByTagName("con:afterRequestScript");
            if (afterRequestScriptNodes.getLength() > 0) {
                if (afterRequestScriptNodes.item(0) != null)
                    afterRequestScript = afterRequestScriptNodes.item(0).getTextContent().trim();
            }

            NodeList constraintsNodes = restMockService.getElementsByTagName("con:minApplicationDelay");
            if (constraintsNodes.getLength() > 0) {
                if (constraintsNodes.item(0) != null)
                    delay = Integer.parseInt(constraintsNodes.item(0).getTextContent().trim());
            }
            System.out.println("delay " + delay);

            int port = 0;
            String autoStart = "false";
            boolean routeModeEnabled = false;
            String routeEndpoint = "";

            String httpSecure = "false";
            if (restMockServiceNodes.getLength() > 0) {
                Element mockservice = (Element) restMockServiceNodes.item(0);
                port = Integer.parseInt(mockservice.getAttribute("port"));
                serviceName = (mockservice.getAttribute("name"));
                autoStart = (mockservice.getAttribute("autoStart"));
                httpSecure = Boolean.parseBoolean((mockservice.getAttribute("httpSecure"))) ? "true" : "false";
                routeModeEnabled = Boolean.parseBoolean((mockservice.getAttribute("routeModeEnabled"))) ? true : false;
                routeEndpoint = (mockservice.getAttribute("routeEndpoint"));
                System.out.println("http secure " + httpSecure);
            }

            if (result != null) {
                // store to db
                datasourceEnabled = true;
                System.out.println("DataSource found and type is Excel");
                // parse the datasource xml and store definition into registry
                registry = parseDataSources(document, serviceName);

            } else {
                Logger.getInstance().info("DataSource is not enabled for service " + serviceName);
            }

            NodeList restMockActions = document.getElementsByTagName("con:restMockAction");

            for (int i = 0; i < restMockActions.getLength(); i++) {
                Element action = (Element) restMockActions.item(i);
                String resourcePath = action.getAttribute("resourcePath");
                String method = action.getAttribute("method");
                String name = action.getAttribute("name");
                NodeList dispatchPathNodes = action.getElementsByTagName("con:dispatchPath");
                String script = null;
                if (dispatchPathNodes.getLength() > 0) {
                    if (dispatchPathNodes.item(0) != null)
                        script = dispatchPathNodes.item(0).getTextContent().trim();
                }

                NodeList dispatchStyleNodes = action.getElementsByTagName("con:dispatchStyle");
                String dispatchStyle = null;
                if (dispatchStyleNodes.getLength() > 0) {
                    if (dispatchStyleNodes.item(0) != null)
                        dispatchStyle = dispatchStyleNodes.item(0).getTextContent().trim();
                }

                NodeList defaultResponseNodes = action.getElementsByTagName("con:defaultResponse");
                String defaultResponse = null;
                if (defaultResponseNodes.getLength() > 0) {
                    if (defaultResponseNodes.item(0) != null)
                        defaultResponse = defaultResponseNodes.item(0).getTextContent().trim();
                }

                List<MockResponse> mockResponses = parseMockResponses(action, resourcePath, serviceName);

                // Initialize Route with ResponseGenerator and mock responses
                ResponseGenerator generator = null;
                if (dispatchStyle.equals("SCRIPT")) {
                    generator = new GroovyScriptResponseGenerator(script); // You can customize this
                } else if (dispatchStyle.equals("SEQUENCE")) {
                    generator = new SequenceResponseGenerator(routeModeEnabled);
                }

                String contentType = "";
                BaseRoute route = new RestRoute(serviceName, generator, mockResponses, defaultResponse, resourcePath,
                        method, name, dispatchStyle, contentType);
                routes.add(route);

            }

            parsedObj.setRoutes(routes);
            parsedObj.setPort(port);
            parsedObj.setAutoStart(autoStart);
            parsedObj.setName(serviceName);
            if (httpSecure.equalsIgnoreCase("false")) {
                parsedObj.setProtocol("HTTP");
            } else
                parsedObj.setProtocol("HTTPS");
            parsedObj.setAfterRequestScript(afterRequestScript);
            parsedObj.setStartScript(startScript);
            parsedObj.setStopScript(stopScript);
            parsedObj.setOnRequestScript(onRequestScript);
            parsedObj.setType("Rest");
            parsedObj.setDelay(delay);
            parsedObj.setHttpSecure(httpSecure);
            parsedObj.setXmlFileContent(Files.readString(xmlFile.toPath()));
            parsedObj.setDatasource(datasourceEnabled);
            parsedObj.setdataSourceRegistry(registry);
            parsedObj.setRouteEndpoint(routeEndpoint);
            parsedObj.setRouteModeEnabled(routeModeEnabled);
            return parsedObj;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace();
            Logger.getInstance().error("Error in rest xml parsing of service " + serviceName + e);
            parsedObj.setexception(e);
            return parsedObj;
        }
    }

    private List<MockResponse> parseMockResponses(Element action, String operationName, String serviceName) {

        List<MockResponse> mockResponses = new ArrayList<>();

        NodeList responseNodes = action.getElementsByTagName("con:response");
        for (int j = 0; j < responseNodes.getLength(); j++) {
            Element responseNode = (Element) responseNodes.item(j);

            String name = responseNode.getAttribute("name");
            int statusCode = Integer.parseInt(responseNode.getAttribute("httpResponseStatus"));
            String mediaType = responseNode.getAttribute("mediaType");

            // Extract response body from <con:responseContent>
            String responseBody = "";
            NodeList responseContentNodes = responseNode.getElementsByTagName("con:responseContent");
            if (responseContentNodes.getLength() > 0) {
                responseBody = responseContentNodes.item(0).getTextContent().trim();
            }

            String responseScript = "";
            NodeList responseScriptNodes = responseNode.getElementsByTagName("con:script");
            if (responseScriptNodes.getLength() > 0) {
                responseScript = responseScriptNodes.item(0).getTextContent().trim();
            }

            // Parse headers
            Headers headers = parseHeaders(mediaType);

            // Create MockResponse object
            MockResponse mockResponse = new MockResponse(name, headers, statusCode, responseBody.getBytes(),
                    responseScript, operationName, serviceName);
            mockResponses.add(mockResponse);
        }

        return mockResponses;
    }

    private Headers parseHeaders(String mediaType) {
        Headers headers = new Headers();
        headers.add("Content-Type", mediaType);
        return headers;
    }

    private String getResponseAttribute(Element action, String attributeName) {
        NodeList responses = action.getElementsByTagName("con:responseContent");
        if (responses.getLength() > 0) {
            Element response = (Element) responses.item(0);
            return response.getAttribute(attributeName);
        }
        return null;
    }

    private String getResponseBody(Element action) {
        NodeList responses = action.getElementsByTagName("con:responseContent");
        if (responses.getLength() > 0) {
            Element response = (Element) responses.item(0);
            return response.getTextContent().trim();
        }
        return null;
    }
}
