package org.framework.properties;

import com.sun.net.httpserver.Headers;

import java.util.List;

public class HttpRequest {

    String queryString;
    String requestURI;
    Headers headers;

    public HttpRequest(String URI, String queryString, Headers headers) {
        this.requestURI = URI;
        this.queryString = queryString;
        this.headers = headers;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getHeader(String headerName) {
        List<String> headerValue = this.headers.get(headerName);
        if (headerValue != null)
            return headerValue.getFirst();
        return null;
    }
}
