package org.framework.properties;

import groovy.lang.GroovyObjectSupport;
import org.framework.core.AbstractService;
import org.framework.core.BaseRoute;

import java.util.HashMap;
import java.util.Map;

public class Context extends GroovyObjectSupport {
    public AbstractService mockService;

    public void setMockService(AbstractService mockService) {
        this.mockService = mockService;
    }

    public BaseRoute getMockOperation() {
        return mockOperation;
    }

    public void setMockOperation(BaseRoute mockOperation) {
        this.mockOperation = mockOperation;
    }

    private BaseRoute mockOperation;

    private final Map<String, Object> dynamicProperties = new HashMap<>();

    public Map<String, Object> getRespTimeProperties() {
        return respTimeProperties;
    }

    private final Map<String, Object> respTimeProperties = new HashMap<>();

    public Context(AbstractService service) {
        super();
        this.mockService = service;
    }

    public Context(Context context) {
        super();
        this.mockService = context.mockService;
    }

    public AbstractService getMockService() {
        return mockService;
    }

    // Handle dynamic properties: Groovy will use this method for context.name =
    // value
    @Override
    public void setProperty(String name, Object value) {
        if (!"mockService".equals(name)) {
            dynamicProperties.put(name, value);
            // System.out.println("Setting property: " + name + " = " + value);
        }
    }

    public void setPropertyValue(String name, Object value) {
        if (!"mockService".equals(name)) {
            dynamicProperties.put(name, value);
            // System.out.println("Setting property: " + name + " = " + value);
        }
    }

    public String getPropertyValue(String name) {

        if (!"mockService".equals(name)) {
            return dynamicProperties.get(name).toString();
            // System.out.println("Setting property: " + name + " = " + value);
        }
        return name;
    }

    // Handle dynamic properties: Groovy will use this method for context.name
    @Override
    public Object getProperty(String name) {
        if (!"mockService".equals(name)) {
            System.out.println("getting property: " + name + " = " + name);
            return dynamicProperties.get(name);
        }

        return mockService;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return this.getMetaClass().invokeMethod(this, name, args);
    }

    public Map<String, Object> getDynamicProperties() {
        return dynamicProperties;
    }
}
