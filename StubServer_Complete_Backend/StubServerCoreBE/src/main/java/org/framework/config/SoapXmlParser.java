package org.framework.config;

import org.framework.core.BaseRoute;
import org.framework.core.ParsedXMLObject;
import org.framework.core.ResponseGenerator;
import org.framework.core.impl.GroovyScriptResponseGenerator;
import org.framework.core.impl.SequenceResponseGenerator;
import org.framework.datasource.DataSourceRegistry;
import org.framework.properties.MockResponse;
import org.framework.services.soap.SoapRoute;
import org.framework.utils.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

public class SoapXmlParser extends Parser {
    public ParsedXMLObject parseSoapRoutes(File xmlFile) throws Exception {
        ParsedXMLObject parsedObj = new ParsedXMLObject();
        try {
            List<BaseRoute> routes = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            System.out.println("Document parsed: " + (document != null));

            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile("//*[local-name()='content']");
            String content = expr.evaluate(document);

            String expression =
                    "//*[local-name()='dataSource' and @type='Excel']" +
                    " | " +
                    "//*[local-name()='File']/*[local-name()='FileType']";

            XPathExpression xPathExpr = xpath.compile(expression);

            Object result = xPathExpr.evaluate(document, XPathConstants.NODE);
            DataSourceRegistry registry = null;
            boolean datasourceEnabled = false;
            boolean routeModeEnabled = false;
            String routeEndpoint = "";

            NodeList soapMockServiceNodes = document.getElementsByTagName("con:mockService");
            Element soapMockService = (Element) soapMockServiceNodes.item(0);
            int delay = 0;

            String startScript = null, stopScript = null, onRequestScript = null, afterRequestScript = null;
            NodeList startScriptNodes = soapMockService.getElementsByTagName("con:startScript");
            if (startScriptNodes.getLength() > 0) {
                if (startScriptNodes.item(0) != null)
                    startScript = startScriptNodes.item(0).getTextContent().trim();
            }

            NodeList stopScriptNodes = soapMockService.getElementsByTagName("con:stopScript");
            if (stopScriptNodes.getLength() > 0) {
                if (stopScriptNodes.item(0) != null)
                    stopScript = stopScriptNodes.item(0).getTextContent().trim();
            }

            NodeList onRequestScriptNodes = soapMockService.getElementsByTagName("con:onRequestScript");
            if (onRequestScriptNodes.getLength() > 0) {
                if (onRequestScriptNodes.item(0) != null)
                    onRequestScript = onRequestScriptNodes.item(0).getTextContent().trim();
            }

            NodeList afterRequestScriptNodes = soapMockService.getElementsByTagName("con:afterRequestScript");
            if (afterRequestScriptNodes.getLength() > 0) {
                if (afterRequestScriptNodes.item(0) != null)
                    afterRequestScript = afterRequestScriptNodes.item(0).getTextContent().trim();
            }

            NodeList constraintsNodes = soapMockService.getElementsByTagName("con:minApplicationDelay");
            if (constraintsNodes.getLength() > 0) {
                if (constraintsNodes.item(0) != null)
                    delay = Integer.parseInt(constraintsNodes.item(0).getTextContent().trim());
            }
            System.out.println("delay " + delay);

            int port = 0;
            String autoStart = "false";
            String httpSecure = "false";
            String serviceName = null;
            if (result != null) {
                //store to db
                datasourceEnabled = true;
                System.out.println("DataSource found and type is Excel");
                //parse the datasource xml and store definition into registry
                registry = parseDataSources(document, serviceName);

            } else {
                Logger.getInstance().info("DataSource is not enabled for service " + serviceName);
            }
            if (soapMockServiceNodes.getLength() > 0) {
                Element mockservice = (Element) soapMockServiceNodes.item(0);
                port = Integer.parseInt(mockservice.getAttribute("port"));
                serviceName = (mockservice.getAttribute("name"));
                autoStart = (mockservice.getAttribute("autoStart"));
                httpSecure = Boolean.parseBoolean((mockservice.getAttribute("httpSecure"))) ? "true" : "false";
                System.out.println("http secure " + httpSecure);
                routeModeEnabled = Boolean.parseBoolean((mockservice.getAttribute("routeModeEnabled"))) ? true : false;
                routeEndpoint = (mockservice.getAttribute("routeEndpoint"));
                System.out.println("http secure " + httpSecure);
            }

            NodeList soapMockActions = document.getElementsByTagName("con:mockOperation");

            for (int i = 0; i < soapMockActions.getLength(); i++) {
                Element action = (Element) soapMockActions.item(i);
                String operation = action.getAttribute("operation");
                String soap_interface = action.getAttribute("interface");
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

                List<MockResponse> mockResponses = parseMockResponses(action);

                // Initialize Route with ResponseGenerator and mock responses
                ResponseGenerator generator = null;
                if (dispatchStyle.equals("SCRIPT")) {
                    generator = new GroovyScriptResponseGenerator(script); // You can customize this
                } else if (dispatchStyle.equals("SEQUENCE")) {
                    generator = new SequenceResponseGenerator();
                }
                String contentType = "Xml";
                BaseRoute route = new SoapRoute(serviceName, generator, mockResponses, defaultResponse, soap_interface, operation, name, dispatchStyle, contentType);
                routes.add(route);
            }

            parsedObj.setRoutes(routes);
            parsedObj.setPort(port);
            parsedObj.setAutoStart(autoStart);
            parsedObj.setName(serviceName);
            parsedObj.setHttpSecure(httpSecure);
            if (httpSecure.equalsIgnoreCase("false")) {
                parsedObj.setProtocol("HTTP");
            } else
                parsedObj.setProtocol("HTTPS");
            parsedObj.setAfterRequestScript(afterRequestScript);
            parsedObj.setStartScript(startScript);
            parsedObj.setStopScript(stopScript);
            parsedObj.setOnRequestScript(onRequestScript);
            parsedObj.setType("Soap");
            parsedObj.setDelay(delay);
            parsedObj.setXmlFileContent(Files.readString(xmlFile.toPath()));
            parsedObj.setWsdlcontent(content);
            parsedObj.setDatasource(datasourceEnabled);
            parsedObj.setdataSourceRegistry(registry);
            parsedObj.setRouteEndpoint(routeEndpoint);
            parsedObj.setRouteModeEnabled(routeModeEnabled);
            return parsedObj;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Logger.getInstance().error("Error occurred in soap parser" + "\n" + sw.toString());
            parsedObj.setexception(e);

        }
        return parsedObj;
    }

    private List<MockResponse> parseMockResponses(Element action) {

        List<MockResponse> mockResponses = new ArrayList<>();

        NodeList responseNodes = action.getElementsByTagName("con:response");
        for (int j = 0; j < responseNodes.getLength(); j++) {
            Element responseNode = (Element) responseNodes.item(j);

            String name = responseNode.getAttribute("name");
            int statusCode = Integer.parseInt(responseNode.getAttribute("httpResponseStatus"));

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

            // Create MockResponse object
            MockResponse mockResponse = new MockResponse(name, statusCode, responseBody.getBytes(), responseScript);
            mockResponses.add(mockResponse);
        }

        return mockResponses;
    }
}
