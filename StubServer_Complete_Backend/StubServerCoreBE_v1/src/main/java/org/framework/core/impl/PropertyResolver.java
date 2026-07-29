package org.framework.core.impl;

import org.framework.properties.Context;

import java.util.HashMap;
import java.util.Map;

public class PropertyResolver {
    private final Context context;

    public PropertyResolver(Context context) {
        this.context = context;
    }

    private final Map<String, String> mockServiceProperties = new HashMap<>();
    private final Map<String, String> globalProperties = new HashMap<>();

    // Add properties to MockService scope
    public void setMockServiceProperty(String name, String value) {
        mockServiceProperties.put(name, value);
    }

    public void setGlobalProperty(String name, String value) {
        globalProperties.put(name, value);
    }

    // Resolve property based on scope
    public String resolveProperty(String placeholder) {
        if (placeholder.startsWith("MockService#")) {
            String propertyName = placeholder.substring("MockService#".length());
            return mockServiceProperties.getOrDefault(propertyName, "UNRESOLVED");
        } else if (placeholder.startsWith("Global#")) {
            String propertyName = placeholder.substring("Global#".length());
            return globalProperties.getOrDefault(propertyName, "UNRESOLVED");
        } else {
            Object value = context.getProperty(placeholder);
            return value != null ? value.toString() : null;
        }
        //return "UNRESOLVED";
    }
}
