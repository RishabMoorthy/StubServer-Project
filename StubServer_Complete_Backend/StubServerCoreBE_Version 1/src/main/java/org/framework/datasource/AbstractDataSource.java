package org.framework.datasource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDataSource implements DataSource {
    private final Map<String, Object> properties = new HashMap<>();

    @Override
    public void setPropertyValue(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            throw new DataSourceException("Property key cannot be null or empty.");
        }
        properties.put(key, value);
    }

    @Override
    public Object getPropertyValue(String key) {
        return properties.get(key);
    }

    protected String getStringProperty(String key) {
        Object value = properties.get(key);
        return value == null ? null : String.valueOf(value);
    }

    protected int getIntProperty(String key, int defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            throw new DataSourceException("Invalid integer value for property '" + key + "': " + value, e);
        }
    }

    protected boolean getBooleanProperty(String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value).trim());
    }

    protected Map<String, Object> getAllProperties() {
        return Collections.unmodifiableMap(properties);
    }
}
