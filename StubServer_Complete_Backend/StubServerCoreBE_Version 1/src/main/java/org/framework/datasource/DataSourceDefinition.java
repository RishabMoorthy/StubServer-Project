package org.framework.datasource;

import java.util.HashMap;
import java.util.Map;

public class DataSourceDefinition {

    private final String name;
    private final DataSourceType type;
    private final AccessMode accessMode;
    private final Map<String, Object> properties = new HashMap<>();

    public DataSourceDefinition(String name, DataSourceType type, AccessMode accessMode) {
        this.name = name;
        this.type = type;
        this.accessMode = accessMode;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public String getName() {
        return name;
    }

    public DataSourceType getType() {
        return type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public DataSourceDefinition addProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }
}
