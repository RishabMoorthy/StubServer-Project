package org.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XPathPreprocessor {

    public static String preprocessXPathForWildcards(String xpathExpression) {
        // Regex to find //*:elementName or /*:elementName patterns
        Pattern wildcardElementPattern = Pattern.compile("(/?)(\\*):([a-zA-Z0-9_-]+)");
        Matcher elementMatcher = wildcardElementPattern.matcher(xpathExpression);
        StringBuffer sb = new StringBuffer();

        while (elementMatcher.find()) {
            String slash = elementMatcher.group(1);
            String localName = elementMatcher.group(3);
            elementMatcher.appendReplacement(sb,
                    Matcher.quoteReplacement(slash) + "*[local-name()='" + Matcher.quoteReplacement(localName) + "']");
        }
        elementMatcher.appendTail(sb);

        String processedXPath = sb.toString();
        sb = new StringBuffer(); // Reset for attribute processing

        // Regex for attributes: [*:attributeName='value']
        Pattern wildcardAttributePattern = Pattern.compile("(\\[)(\\*):([a-zA-Z0-9_-]+)");
        Matcher attributeMatcher = wildcardAttributePattern.matcher(processedXPath);

        while (attributeMatcher.find()) {
            String bracket = attributeMatcher.group(1);
            String localName = attributeMatcher.group(3);
            attributeMatcher.appendReplacement(sb,
                    Matcher.quoteReplacement(bracket) + "local-name()='" + Matcher.quoteReplacement(localName) + "']");
        }
        attributeMatcher.appendTail(sb);

        return sb.toString();
    }
}
