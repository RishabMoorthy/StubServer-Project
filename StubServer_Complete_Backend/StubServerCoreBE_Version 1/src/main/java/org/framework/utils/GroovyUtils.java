package org.framework.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.framework.properties.Context;
import org.w3c.dom.Document;
import java.io.ByteArrayInputStream;

public class GroovyUtils {
    private final Object context;

    public GroovyUtils(Context context) {
        this.context = context; // optionally use this
    }

    public XmlHolder getXmlHolder(String xmlContent) throws Exception {
        return new XmlHolder(xmlContent);
    }
}
