package org.framework.utils;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

public class AddCustomResponseToOperation {
    public synchronized String addMockResponseInXml(String operationName, String mockResponseName, String xmlPath) {
        try {
            // Load the XML file
            File xmlFile = new File(xmlPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Important for handling "con:" prefix
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            // Find the customOperation by name
            NodeList operations = doc.getElementsByTagName("con:customOperation");
            for (int i = 0; i < operations.getLength(); i++) {
                Element operation = (Element) operations.item(i);
                if (operationName.equals(operation.getAttribute("name"))) {

                    // Create the customResponse element
                    Element customResponse = doc.createElement("con:customResponse");
                    customResponse.setAttribute("name", mockResponseName);
                    customResponse.setAttribute("id", "a1309c00-41e6-485e-a2d7-6db9556b9ff5");

                    // Add empty settings and script
                    customResponse.appendChild(doc.createElement("con:settings"));
                    customResponse.appendChild(doc.createElement("con:script"));

                    // Add responseProperties with one property
                    Element responseProperties = doc.createElement("con:responseProperties");
                    Element property = doc.createElement("con:property");

                    Element name = doc.createElement("con:name");
                    name.setTextContent("responseData");

                    Element value = doc.createElement("con:value");
                    value.setTextContent("");

                    property.appendChild(name);
                    property.appendChild(value);
                    responseProperties.appendChild(property);
                    customResponse.appendChild(responseProperties);

                    // Append to operationProperties inside the operation
                    NodeList children = operation.getElementsByTagName("con:operationProperties");
                    if (children.getLength() > 0) {
                        children.item(0).appendChild(customResponse);
                    } else {
                        // If operationProperties doesn't exist, create it
                        Element operationProperties = doc.createElement("con:operationProperties");
                        operationProperties.appendChild(customResponse);
                        operation.appendChild(operationProperties);
                    }

                    break; // Stop after finding the correct operation
                }
            }

            // Save the updated XML
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            System.out.println("Custom response added to SenderReq successfully!");

            // Also return the updated XML as a String
            StringWriter writer = new StringWriter();
            StreamResult stringResult = new StreamResult(writer);
            transformer.transform(source, stringResult);
            return writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String addMockResponseByteInXml(String operationName, String mockResponseName, String xmlContent,
            byte[] responseContent) {
        try {
            // Clean XML string
            xmlContent = xmlContent.trim();
            if (xmlContent.startsWith("\uFEFF")) {
                xmlContent = xmlContent.substring(1);
            }

            // Parse XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

            // Normalize document
            doc.getDocumentElement().normalize();

            // Remove whitespace-only text nodes to prevent XML size growth
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            NodeList emptyTextNodes = (NodeList) xpath.evaluate("//text()[normalize-space(.)='']", doc,
                    XPathConstants.NODESET);
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node node = emptyTextNodes.item(i);
                node.getParentNode().removeChild(node);
            }

            // Namespace context for XPath
            xpath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    return "http://eviware.com/soapui/config"; // Adjust if needed
                }

                public String getPrefix(String uri) {
                    return null;
                }

                public Iterator getPrefixes(String uri) {
                    return null;
                }
            });

            // XPath to find <con:value>
            String expression = String.format(
                    "//con:customOperation[@name='%s']//con:customResponse[@name='%s']//con:property[con:name='responseData']/con:value",
                    operationName, mockResponseName);

            Node valueNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);

            if (valueNode != null) {
                // Update existing value
                valueNode.setTextContent(DatatypeConverter.printHexBinary(responseContent));
            } else {
                // If <con:value> doesn't exist, create it
                String propertyExpr = String.format(
                        "//con:customOperation[@name='%s']//con:customResponse[@name='%s']//con:property[con:name='responseData']",
                        operationName, mockResponseName);
                Node propertyNode = (Node) xpath.evaluate(propertyExpr, doc, XPathConstants.NODE);
                if (propertyNode != null) {
                    Element valueElement = doc.createElement("con:value");
                    valueElement.setTextContent(DatatypeConverter.printHexBinary(responseContent));
                    propertyNode.appendChild(valueElement);
                }
            }

            // Convert back to String without adding extra spaces
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no"); // Prevent repeated indentation
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized String addMockResponseContentInXml(
            String operationName, String mockResponseName, String xmlContent, String responseContent) {
        try {
            // File xmlFile = new File(xmlPath);

            // Parse existing XML with namespace awareness OFF (prefix-only handling)
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlContent)));
            doc.getDocumentElement().normalize();

            // Find <con:restMockAction name="...">
            NodeList actions = doc.getElementsByTagName("con:restMockAction");
            Element matchedAction = null;
            for (int i = 0; i < actions.getLength(); i++) {
                Element action = (Element) actions.item(i);
                if (operationName.equals(action.getAttribute("name"))) {
                    matchedAction = action;
                    break;
                }
            }
            if (matchedAction == null) {
                return "Operation not found: " + operationName;
            }

            // Find <con:response name="..."> under the matched action
            NodeList responses = matchedAction.getElementsByTagName("con:response");
            Element matchedResponse = null;
            for (int j = 0; j < responses.getLength(); j++) {
                Element response = (Element) responses.item(j);
                if (mockResponseName.equals(response.getAttribute("name"))) {
                    matchedResponse = response;
                    break;
                }
            }
            if (matchedResponse == null) {
                return "Response not found: " + mockResponseName;
            }

            // Get or create <con:responseContent>
            NodeList contentNodes = matchedResponse.getElementsByTagName("con:responseContent");
            Element contentElement;
            if (contentNodes.getLength() > 0) {
                contentElement = (Element) contentNodes.item(0);
                // Remove all existing children (text, CDATA, comments, etc.)
                while (contentElement.hasChildNodes()) {
                    contentElement.removeChild(contentElement.getFirstChild());
                }
            } else {
                // Create with the 'con' prefix (no namespace binding required for your case)
                contentElement = doc.createElement("con:responseContent");
                matchedResponse.appendChild(contentElement);
            }

            // SAFETY: split CDATA if payload contains "]]>" to avoid malformed XML
            // This produces multiple contiguous CDATA sections but no extra whitespace.
            String safeContent = responseContent.replace("]]>", "]]]]><![CDATA[>");

            // Append content inside CDATA (ReadyAPI commonly uses CDATA for bodies)
            contentElement.appendChild(doc.createCDATASection(safeContent));

            // 7) Serialize to string WITHOUT any indentation (no extra spaces/new lines)
            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer transformer = tfFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "no"); // critical: keep original whitespace
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            // Best-effort hint to preserve CDATA for 'responseContent' (namespace-agnostic
            // local name)
            transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "responseContent");

            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            String updatedXml = sw.toString();

            // 8) Sanity re-parse the result to ensure we produced valid XML
            dBuilder.parse(new InputSource(new StringReader(updatedXml)));

            // 9) Return the updated XML string
            return updatedXml;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating XML: " + e.getMessage();
        }
    }
}
