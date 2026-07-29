package org.framework.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WrappedXmlHolder {
    private final org.framework.utils.XmlHolder originalXmlHolder;
    private final NamespaceContext namespaceContext;
    private XPath xpathInstance; // Store the XPath instance

    // Constructor now takes the XML content
    public WrappedXmlHolder(String xmlContent) throws Exception {
        this.namespaceContext = new DynamicNamespaceContext(xmlContent); // Create dynamic context

        // Instantiate the original XmlHolder using GroovyUtils or direct instantiation
        // This line depends on how XmlHolder is typically created by GroovyUtils
        // Assuming GroovyUtils has a getXmlHolder(String) method that returns an
        // XmlHolder
        org.framework.utils.GroovyUtils tempGroovyUtils = new org.framework.utils.GroovyUtils(null); // Assuming this is
                                                                                                       // how it's done
        this.originalXmlHolder = tempGroovyUtils.getXmlHolder(xmlContent);

        // Attempt to get the internal XPath instance via reflection
        try {
            Field xpathField = originalXmlHolder.getClass().getDeclaredField("xpath"); // Assuming field name is "xpath"
            xpathField.setAccessible(true);
            this.xpathInstance = (XPath) xpathField.get(originalXmlHolder);
            this.xpathInstance.setNamespaceContext(this.namespaceContext); // Set the context immediately

        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println(
                    "Warning: Could not set NamespaceContext via reflection. XPath may not be namespace-aware: "
                            + e.getMessage());
            // Fallback: Try to use a setter if available (less likely for private fields)
            try {
                Method setXPathMethod = originalXmlHolder.getClass().getMethod("setXPath", XPath.class);
                XPath newXPath = javax.xml.xpath.XPathFactory.newInstance().newXPath();
                newXPath.setNamespaceContext(this.namespaceContext);
                setXPathMethod.invoke(originalXmlHolder, newXPath);
                this.xpathInstance = newXPath; // Update the stored XPath
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException innerE) {
                System.err.println("Could not set NamespaceContext via setter either: " + innerE.getMessage());
                // If all fails, proceed without setting context via reflection/setter
                // This means the XPath might still fail for namespace-aware queries
                this.xpathInstance = javax.xml.xpath.XPathFactory.newInstance().newXPath(); // Create a new one
            }
        }
    }

    public String getNodeValue(String xpathExpression) throws Exception {
        // Ensure the NamespaceContext is set on the internal XPath instance before use
        // This is a safety measure in case the XPath instance was reset or re-created
        if (xpathInstance != null && xpathInstance.getNamespaceContext() == null) {
            xpathInstance.setNamespaceContext(this.namespaceContext);
        }

        // Call the original XmlHolder's getNodeValue, which should now use the
        // correctly configured XPath
        return originalXmlHolder.getNodeValue(xpathExpression);
    }
}
