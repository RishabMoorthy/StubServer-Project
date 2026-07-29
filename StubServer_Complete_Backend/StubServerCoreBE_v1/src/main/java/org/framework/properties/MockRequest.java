package org.framework.properties;

import com.sun.net.httpserver.Headers;
import org.codehaus.jackson.annotate.JsonIgnore;

public class MockRequest {
    Headers requestHeaders;
    String requestContent;
    String path;
    String queryString;
    String method;
    @JsonIgnore
    MockRequest mockRequest;
    String protocol;
    String operation;
    String soap_interface;
    String requestXmlObject;

    private HttpRequest httpRequest;

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @JsonIgnore
    public MockRequest getMockRequest() {
        return mockRequest;
    }

    public void setMockRequest(MockRequest mockRequest) {
        this.mockRequest = mockRequest;
    }

    @JsonIgnore
    public String getRequest() {
        if (this.requestContent == null || this.requestContent.isEmpty())
            return this.queryString;
        else
            return this.requestContent;
    }

    public String getRequestXmlObject() {
        return requestXmlObject;
    }

    public void setRequestXmlObject(String requestXmlObject) {
        this.requestXmlObject = requestXmlObject;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSoap_interface() {
        return soap_interface;
    }

    public void setSoap_interface(String soap_interface) {
        this.soap_interface = soap_interface;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Headers getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Headers requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HttpRequest getHttpRequest() {
        return new HttpRequest(path, queryString, requestHeaders);
    }
}
