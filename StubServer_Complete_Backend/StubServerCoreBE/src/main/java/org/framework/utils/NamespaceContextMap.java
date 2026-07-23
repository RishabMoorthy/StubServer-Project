package org.framework.utils;

import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.*;

public class NamespaceContextMap implements NamespaceContext {
    public final Map<String, String> namespaces = new HashMap<>();

    public NamespaceContextMap(Element element) {
        populateNamespaces(element);
    }

    private void populateNamespaces(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            String nodeName = attr.getNodeName();
            System.out.println("node name " + nodeName);
            if (nodeName.startsWith("xmlns:")) {
                String prefix = nodeName.substring(6);
                String uri = attr.getNodeValue();
                System.out.println("uri :" + uri + " prefix " + prefix);
                namespaces.put(prefix, uri);
            }
        }

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                populateNamespaces((Element) child);
            }
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null || prefix.isEmpty() || prefix.equals("*")) {
            return "http://abc.com/ns0";
        }

        return namespaces.getOrDefault(prefix, XMLConstants.NULL_NS_URI);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (entry.getValue().equals(namespaceURI))
                return entry.getKey();
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        List<String> prefixes = new ArrayList<>();
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (entry.getValue().equals(namespaceURI)) {
                prefixes.add(entry.getKey());
            }
        }

        return prefixes.iterator();
    }
}
