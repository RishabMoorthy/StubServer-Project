package org.framework.utils;

import javax.xml.namespace.NamespaceContext;
import javax.xml.XMLConstants;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class DynamicNamespaceContext implements NamespaceContext {

    private final Map<String, String> prefixToUriMap = new HashMap<>();

    public DynamicNamespaceContext(String xmlContent) {
        parseXmlNamespaces(xmlContent);
        prefixToUriMap.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
    }

    private void parseXmlNamespaces(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

            Element root = document.getDocumentElement();
            extractNamespaces(root);
        } catch (Exception e) {
            System.err.println("Error parsing XML for namespaces: " + e.getMessage());
        }
    }

    private void extractNamespaces(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr = (Attr) attributes.item(i);
            String attrName = attr.getName();
            if (attrName.startsWith("xmlns:")) {
                String prefix = attrName.substring(attrName.indexOf(":") + 1);
                String uri = attr.getValue();
                prefixToUriMap.put(prefix, uri);
            } else if (attrName.equals("xmlns")) {
                prefixToUriMap.put(XMLConstants.DEFAULT_NS_PREFIX, attr.getValue());
            }
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("Prefix cannot be null");
        }
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return prefixToUriMap.getOrDefault(XMLConstants.DEFAULT_NS_PREFIX, "");
        }
        return prefixToUriMap.getOrDefault(prefix, XMLConstants.NULL_NS_URI);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> entry : prefixToUriMap.entrySet()) {
            if (entry.getValue().equals(namespaceURI)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return prefixToUriMap.keySet().stream()
                .filter(prefix -> prefixToUriMap.get(prefix).equals(namespaceURI))
                .iterator();
    }
}
