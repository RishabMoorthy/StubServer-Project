package org.framework.core;

import org.framework.properties.MockRequest;
import org.framework.properties.MockResponse;

public class RequestLog {
    MockRequest request;
    MockResponse response;
    String path;
    String timestamp;
    String serviceName;
    int port;
    String status;
    String type;
    String source;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public RequestLog(MockRequest request, MockResponse response, String path, String timestamp, String serviceName, int port, boolean isRunning, String type) {
        this.request = request;
        this.response = response;
        this.path = path;
        this.timestamp = timestamp;
        this.serviceName = serviceName;
        this.port = port;
        this.status = isRunning ? "Running" : "Stopped";
        this.source = response.getSource();
        this.type = type;
    }
    
    public MockRequest getRequest() {
        return request;
    }

    public void setRequest(MockRequest request) {
        this.request = request;
    }

    public MockResponse getResponse() {
        return response;
    }

    public void setResponse(MockResponse response) {
        this.response = response;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
