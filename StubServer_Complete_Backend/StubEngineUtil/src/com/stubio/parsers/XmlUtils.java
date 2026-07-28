package com.stubio.parsers;

import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public final class XmlUtils {

    private XmlUtils() {}

    public static List<Element> childElements(Node parent) {

        List<Element> elements = new ArrayList<>();

        NodeList nodes = parent.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {

            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }

        return elements;
    }

    public static String text(Node node) {
        return node == null ? "" : node.getTextContent().trim();
    }

    public static String attr(Node node, String name) {

        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return "";
        }

        return ((Element) node).getAttribute(name);
    }

    public static String local(Node node) {

        if (node.getLocalName() != null) {
            return node.getLocalName();
        }

        String name = node.getNodeName();

        int idx = name.indexOf(':');

        return idx > 0 ? name.substring(idx + 1) : name;
    }
}
