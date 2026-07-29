package org.framework.utils;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jayway.jsonpath.JsonPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class JsonXmlValueExtractor {

    public static void collectValues(Object node, List<Object> values) {
        if (node instanceof Map<?, ?>) {
            ((Map<?, ?>) node).values().forEach(value -> collectValues(value, values));
        } else if (node instanceof List<?>) {
            ((List<?>) node).forEach(value -> collectValues(value, values));
        } else {
            if (node instanceof String && ((String) node).startsWith("${#MockResponse#Request")) {
                System.out.println("node " + node);
                values.add(node);
            }
        }
    }

    public static List<Object> extractValuesFromJson(String jsonString) throws Exception {

        // Step 2: Ensure all ${...} expressions are quoted to make valid JSON
        jsonString = quoteUnquotedExpressions(jsonString);

        // Step 3: Parse JSON
        System.out.println("after resolving: " + jsonString);
        Object jsonObject = JsonPath.parse(jsonString).json();

        // Step 4: Collect values
        List<Object> values = new ArrayList<>();
        collectValues(jsonObject, values);
        return values;
    }

    public static List<Object> extractValuesFromXml(String xmlString) throws Exception {
        List<Object> matchingValues = new ArrayList<>();
        String startsWith = "${#MockResponse#Request";

        // Parse XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // Optional, if you have namespaces
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

        // Start traversal
        collectNodeValues(doc.getDocumentElement(), startsWith, matchingValues);

        return matchingValues;
    }

    private static void collectNodeValues(Node node, String startsWith, List<Object> values) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            String text = node.getTextContent().trim();
            if (text.startsWith(startsWith)) {
                System.out.println("Matched value: " + text);
                values.add(text);
            }
        }

        // Traverse child nodes
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            collectNodeValues(children.item(i), startsWith, values);
        }
    }

    public static String quoteUnquotedExpressions(String json) {
        // Wrap unquoted ${...} expressions in quotes to make valid JSON
        return json.replaceAll("(:\\s*)(\\$\\{[^}]+\\})", "$1\"$2\"");
    }
}
