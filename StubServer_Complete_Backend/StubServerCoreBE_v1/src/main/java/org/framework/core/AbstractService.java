package org.framework.core;

import org.framework.config.ServiceConfig;
import org.framework.datasource.DataSource;
import org.framework.datasource.DataSourceDefinition;
import org.xml.sax.HandlerBase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractService {
    protected ServiceConfig config;

    public ServiceConfig getConfig() {
        return config;
    }

    public void setConfig(ServiceConfig config) {
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected String name;

    private boolean isRunning;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;

    private final Map<String, Object> mockServiceProperties = new HashMap<>();
    // Method to set a dynamic property
    public void setPropertyValue(String name, Object value) {
        mockServiceProperties.put(name, value);
    }

    // Method to get a dynamic property
    public Object getPropertyValue(String name) {
        return mockServiceProperties.get(name);
    }

    public Map<String, Object> getMockServiceProperties() {
        return mockServiceProperties;
    }

    public AbstractService(ServiceConfig config) {
        this.config = config;
        this.name = config.getServiceName();
    }

    public DataSource getDataSource(String name) {
        DataSourceDefinition definition = config.getDataSourceService().getRegistry().getDefinition(name);
        return config.getDataSourceService().getFactory().create(definition);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public abstract void start() throws Exception;
    public abstract void stop();
}
