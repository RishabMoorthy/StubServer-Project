package org.framework.config;

import org.framework.core.BaseRoute;
import org.framework.core.ParsedXMLObject;
import org.framework.core.ResponseGenerator;
import org.framework.core.impl.GroovyScriptResponseGenerator;
import org.framework.core.impl.SequenceResponseGenerator;
import org.framework.datasource.DataSourceRegistry;
import org.framework.properties.MockResponse;
import org.framework.properties.Properties;
import org.framework.services.tcp.TcpRoute;
import org.framework.utils.Logger;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class TcpXmlParser extends Parser {

    public ParsedXMLObject parseRestRoutes(File xmlFile) throws Exception {
        try {
            List<BaseRoute> routes = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            NodeList tcpMockServiceNodes = document.getElementsByTagName("con:customMockService");
            Element tcpMockService = (Element) tcpMockServiceNodes.item(0);
            boolean routeModeEnabled = false;
            String routeEndpoint = "";

            Properties properties = parseTcpProperties(tcpMockService);
            int port = Integer.parseInt(properties.getProperties().get("tcpMockService.port"));
            int delay = 0;
            String startScript = null, stopScript = null, onRequestScript = null, afterRequestScript = null;
            NodeList startScriptNodes = tcpMockService.getElementsByTagName("con:startScript");
            if (startScriptNodes.getLength() > 0) {
                if (startScriptNodes.item(0) != null)
                    startScript = startScriptNodes.item(0).getTextContent().trim();
            }

            NodeList stopScriptNodes = tcpMockService.getElementsByTagName("con:stopScript");
            if (stopScriptNodes.getLength() > 0) {
                if (stopScriptNodes.item(0) != null)
                    stopScript = stopScriptNodes.item(0).getTextContent().trim();
            }

            NodeList onRequestScriptNodes = tcpMockService.getElementsByTagName("con:onRequestScript");
            if (onRequestScriptNodes.getLength() > 0) {
                if (onRequestScriptNodes.item(0) != null)
                    onRequestScript = onRequestScriptNodes.item(0).getTextContent().trim();
            }

            NodeList afterRequestScriptNodes = tcpMockService.getElementsByTagName("con:afterRequestScript");
            if (afterRequestScriptNodes.getLength() > 0) {
                if (afterRequestScriptNodes.item(0) != null)
                    afterRequestScript = afterRequestScriptNodes.item(0).getTextContent().trim();
            }

            NodeList propertiesNodes = tcpMockService.getElementsByTagName("con:properties");
            for (int i = 0; i < propertiesNodes.getLength(); i++) {
                Element action = (Element) propertiesNodes.item(i);

                NodeList propertyNameNodes = action.getElementsByTagName("con:name");
                String propertyName = null;
                if (propertyNameNodes.getLength() > 0) {
                    if (propertyNameNodes.item(0) != null)
                        propertyName = propertyNameNodes.item(0).getTextContent().trim();
                }
            }

            // XPath to find dataSource with type Excel
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = "//*[local-name()='dataSource' and @type='Excel']" +
                    " | " +
                    "//*[local-name()='File']/*[local-name()='FileType']";

            XPathExpression xPathExpr = xpath.compile(expression);

            Object result = xPathExpr.evaluate(document, XPathConstants.NODE);
            DataSourceRegistry registry = null;
            boolean datasourceEnabled = false;

            String autoStart = "false";
            String serviceName = null;
            if (tcpMockServiceNodes.getLength() > 0) {
                Element mockservice = (Element) tcpMockServiceNodes.item(0);
                serviceName = (mockservice.getAttribute("name"));
                autoStart = (mockservice.getAttribute("autoStart"));
            }

            NodeList tcpMockActions = tcpMockService.getElementsByTagName("con:customOperation");

            for (int i = 0; i < tcpMockActions.getLength(); i++) {
                Element action = (Element) tcpMockActions.item(i);

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

                if (result != null) {
                    // store to db
                    datasourceEnabled = true;
                    System.out.println("DataSource found and type is Excel");
                    // parse the datasource xml and store definition into registry
                    registry = parseDataSources(document, serviceName);
                } else {
                    Logger.getInstance().info("DataSource is not enabled for service" + serviceName);
                }

                List<MockResponse> mockResponses = parseMockResponses(action, name, serviceName);

                // Initialize Route with ResponseGenerator and mock responses
                ResponseGenerator generator = null;
                if (dispatchStyle.equals("SCRIPT")) {
                    generator = new GroovyScriptResponseGenerator(script); // You can customize this
                } else if (dispatchStyle.equals("SEQUENCE")) {
                    generator = new SequenceResponseGenerator();
                }
                String contentType = "Hex";// needs to be updated
                BaseRoute route = new TcpRoute(serviceName, generator, mockResponses, defaultResponse, name,
                        dispatchStyle, contentType);
                routes.add(route);
            }
            ParsedXMLObject parsedObj = new ParsedXMLObject();
            parsedObj.setRoutes(routes);
            parsedObj.setProperties(properties);
            parsedObj.setPort(port);
            parsedObj.setAutoStart(autoStart);
            parsedObj.setName(serviceName);
            parsedObj.setProtocol("TCP");
            parsedObj.setAfterRequestScript(afterRequestScript);
            parsedObj.setStartScript(startScript);
            parsedObj.setStopScript(stopScript);
            parsedObj.setOnRequestScript(onRequestScript);
            parsedObj.setType("Tcp");
            parsedObj.setXmlFileContent(Files.readString(xmlFile.toPath()));
            parsedObj.setDatasource(datasourceEnabled);
            parsedObj.setdataSourceRegistry(registry);
            parsedObj.setRouteEndpoint(routeEndpoint);
            parsedObj.setRouteModeEnabled(routeModeEnabled);
            return parsedObj;
        } catch (Exception e) {
            Logger.getInstance().error("Error in tcp xml parsing " + e);
            return null;
        }
    }

    private List<MockResponse> parseMockResponses(Element action, String operationName, String serviceName) {

        List<MockResponse> mockResponses = new ArrayList<>();

        NodeList responseNodes = action.getElementsByTagName("con:customResponse");
        for (int j = 0; j < responseNodes.getLength(); j++) {
            Element responseNode = (Element) responseNodes.item(j);

            String name = responseNode.getAttribute("name");
            String mediaType = responseNode.getAttribute("value");//

            // Extract response body from <con:responseContent>
            String responseBody = "";
            NodeList responseContentNodes = responseNode.getElementsByTagName("con:value");
            if (responseContentNodes.getLength() > 0) {
                responseBody = responseContentNodes.item(0).getTextContent().trim();
            }

            String responseScript = "";
            NodeList responseScriptNodes = responseNode.getElementsByTagName("con:script");
            if (responseScriptNodes.getLength() > 0) {
                responseScript = responseScriptNodes.item(0).getTextContent().trim();
            }

            // Parse headers
            // Headers headers = parseHeaders(mediaType);

            // Create MockResponse object
            MockResponse mockResponse = new MockResponse(name, HexFormat.of().parseHex(responseBody), responseScript,
                    operationName, serviceName);
            mockResponses.add(mockResponse);
        }

        return mockResponses;
    }

    private Properties parseTcpProperties(Element action) {

        NodeList propertyNodes = action.getElementsByTagName("con:property");
        Properties tcpProperties = new Properties();
        for (int j = 0; j < propertyNodes.getLength(); j++) {
            Element propertyNode = (Element) propertyNodes.item(j);

            // Extract response body from <con:responseContent>
            String value = "";
            NodeList propertyValueNodes = propertyNode.getElementsByTagName("con:value");
            if (propertyValueNodes.getLength() > 0) {
                value = propertyValueNodes.item(0).getTextContent().trim();
            }

            String name = "";
            NodeList propertyNameNodes = propertyNode.getElementsByTagName("con:name");
            if (propertyNameNodes.getLength() > 0) {
                name = propertyNameNodes.item(0).getTextContent().trim();
            }

            // Parse headers
            // Headers headers = parseHeaders(mediaType);

            // Create MockResponse object
            tcpProperties.addProperty(name, value);
        }

        return tcpProperties;
    }
}
