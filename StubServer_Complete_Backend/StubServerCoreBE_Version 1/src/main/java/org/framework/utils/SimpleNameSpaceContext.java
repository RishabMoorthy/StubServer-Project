package org.framework.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;

public class SimpleNameSpaceContext implements NamespaceContext {
    private Map<String, String> prefixMap = new HashMap<String, String>();

    public SimpleNameSpaceContext() {
    }

    public SimpleNameSpaceContext(String prefix, String uri) {
        prefixMap.put(prefix, uri);
    }

    public void addPrefixMapping(String prefix, String uri) {
        prefixMap.put(prefix, uri);
    }

    public String getNamespaceURI(String prefix) {
        if (prefixMap.containsKey(prefix)) {
            return prefixMap.get(prefix);
        }
        return null;
    }

    public String getPrefix(String namespaceURI) {
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
