package org.framework.utils;

import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import java.io.ByteArrayInputStream;

public class XmlHolder {
    private final Document xmlDoc;

    // Put at class level (inside XmlHolder)
    private static final java.util.regex.Pattern DECL_NS_BLOCK = java.util.regex.Pattern.compile(
            // (?is) -> case-insensitive + dotall
            // ^\\s* -> from the beginning, any spaces or newlines
            // (?:declare namespace pfx='uri'; [spaces/newlines])* -> one or more
            // declarations
            "(?is)^\\s*(?:declare\\s+namespace\\s+[A-Za-z_][\\w\\-.]*\\s*=\\s*(?:'[^']*'|\"[^\"]*\")\\s*;\\s*)+");

    /**
     * Strips all leading "declare namespace pfx='uri';" statements from an XPath
     * string.
     * Handles multiple lines, varying whitespace, and both single/double quoted
     * URIs.
     */
    private String stripInlineNamespaceDecls(String xpathExpression) {
        if (xpathExpression == null)
            return null;
        java.util.regex.Matcher m = DECL_NS_BLOCK.matcher(xpathExpression);
        if (m.find()) {
            String stripped = xpathExpression.substring(m.end()).trim();
            System.out.println("[stripInlineNamespaceDecls] stripped declarations. Remaining XPath: " + stripped);
            return stripped;
        }
        System.out.println("[stripInlineNamespaceDecls] no declarations found. Using as-is.");
        return xpathExpression.trim();
    }

    /**
     * Rewrites prefixed node/attribute tests to namespace-agnostic form using
     * local-name().
     * Examples:
     * //ns1:submitDocument/ns2:serviceLevel
     * -> //*[local-name()='submitDocument']/*[local-name()='serviceLevel']
     *
     * //@ns2:attr -> //@*[local-name()='attr']
     *
     * Functions (like local-name()) are not touched.
     */
    private String rewritePrefixedXPathToLocalName(String xPath) {
        if (xPath == null || xPath.isEmpty())
            return xPath;

        String out = xPath;

        // Attributes first: @pfx:name -> @*[local-name()='name']
        out = out.replaceAll("@\\s*([A-Za-z_][\\w\\-.]*)\\:([A-Za-z_][\\w\\-.]*)", "@*[local-name()='$2']");

        // Path steps (after '/' or '|'): /ns:name -> /*[local-name()='name']
        out = out.replaceAll("(?<=/|\\|)\\s*([A-Za-z_][\\w\\-.]*)\\:([A-Za-z_][\\w\\-.]*)", "*[local-name()='$2']");

        // Start-of-string: leading ns:name -> *[local-name()='name']
        out = out.replaceFirst("^\\s*([A-Za-z_][\\w\\-.]*)\\:([A-Za-z_][\\w\\-.]*)", "*[local-name()='$2']");

        // Anywhere else a bare QName appears as a node test (not a function call)
        out = out.replaceAll("(?<![A-Za-z0-9_\\-\\.])([A-Za-z_][\\w\\-.]*)\\:([A-Za-z_][\\w\\-.]*)(?!\\s*\\()",
                "*[local-name()='$2']");

        return out;
    }

    public XmlHolder(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // important for SOAP
        String fixedXml = xmlContent;
        // NEW (CORRECT):
        // String fixedXml = xmlContent.replaceAll("UTF-16", "UTF-8");
        byte[] xmlBytes = fixedXml.getBytes("UTF-8");
        ByteArrayInputStream byteStream = new ByteArrayInputStream(xmlBytes);
        DocumentBuilder builder = factory.newDocumentBuilder();

        xmlDoc = builder.parse(byteStream);
    }

    private String extractEncodingFromXml(String xmlContent) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "<\\?xml[^>]+encoding\\s*=\\s*['\"]([^'\"]+)['\"]",
                java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(xmlContent);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String detectActualEncoding(byte[] bytes) {
        if (bytes.length < 4)
            return "UTF-8";

        // UTF-8 BOM
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            return "UTF-8";
        }

        // UTF-16 BOMs
        if (bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF)
            return "UTF-16BE";
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE)
            return "UTF-16LE";

        // Detect by pattern
        if (bytes[0] == 0x3C && bytes[1] >= 0x20 && bytes[1] <= 0x7E)
            return "UTF-8";
        if (bytes[0] == 0x3C && bytes[1] == 0x00)
            return "UTF-16LE";
        if (bytes[0] == 0x00 && bytes[1] == 0x3C)
            return "UTF-16BE";

        return "UTF-8";
    }

    public String getNodeValue(String xpathExpression) throws Exception {

        xpathExpression = stripInlineNamespaceDecls(xpathExpression);

        // 2) NEW: rewrite any remaining prefixes to local-name()
        xpathExpression = rewritePrefixedXPathToLocalName(xpathExpression);

        xpathExpression = this.preProcess(xpathExpression);
        System.out.println("xpath expression : " + xpathExpression);
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);
            String result = expr.evaluate(xmlDoc);
            return result != null ? result.trim() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getNodeValues(String xpathExpression) throws Exception {
        // Apply your wildcard/prefix pre-processing

        xpathExpression = stripInlineNamespaceDecls(xpathExpression);

        // 2) NEW: rewrite any remaining prefixes to local-name()
        xpathExpression = rewritePrefixedXPathToLocalName(xpathExpression);
        xpathExpression = this.preProcess(xpathExpression);

        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);

            // Evaluate as NODESET to get all matches
            NodeList nodes = (NodeList) expr.evaluate(xmlDoc, javax.xml.xpath.XPathConstants.NODESET);

            java.util.List<String> results = new java.util.ArrayList<>(nodes.getLength());

            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                String val;

                switch (n.getNodeType()) {
                    case Node.ATTRIBUTE_NODE:
                        // For attribute nodes (when XPath selects @attr)
                        val = n.getNodeValue();
                        break;

                    case Node.ELEMENT_NODE:
                    case Node.TEXT_NODE:
                    case Node.CDATA_SECTION_NODE:
                        // For elements/text/cdata use textContent
                        val = n.getTextContent();
                        break;

                    default:
                        // Fallback (comments, PI, etc.) - usually not needed, but safe
                        val = n.getTextContent();
                }

                if (val != null) {
                    val = val.trim();
                    if (!val.isEmpty()) {
                        results.add(val);
                    }
                }
            }

            return results.toArray(new String[0]);

        } catch (Exception e) {
            e.printStackTrace();
            return new String[0]; // safe fallback on error
        }
    }

    public String preProcess(String xPath) {
        String preprocessedXPath = XPathPreprocessor.preprocessXPathForWildcards(xPath);
        System.out.println("preprocessedXPath " + preprocessedXPath);
        return preprocessedXPath;
    }
}
