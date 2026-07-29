package org.framework.properties;

import java.util.HashMap;
import java.util.Map;

public class Properties {
    private Map<String, String> property = new HashMap<String, String>();

    public void addProperty(String name, String value) {
        this.property.put(name, value);
    }

    public Map<String, String> getProperties() {
        return property;
    }
}
