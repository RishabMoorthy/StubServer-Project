package com.stubio.util;

import java.util.LinkedList;

public class Request {

    public Request()
    {}

    public Request(Request request)
    {
        this.contentType = request.getContentType();
        this.requestData = request.getRequestData();
        this.name = request.getName();
        this.requestParameters = request.getRequestParameters();
    }

    private String contentType; // attribute
    private String requestData, name; // <vs:RequestData>
    private LinkedList<Argument> requestParameters = new LinkedList<>(); // <vs:RequestParameters><vs:arg>...</vs:arg></vs:RequestParameters>
    private LinkedList<Argument> headers = new LinkedList<>();
    private LinkedList<Argument> queryParams = new LinkedList<>();

    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRequestData() {
        return requestData;
    }
    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public LinkedList<Argument> getRequestParameters() {
        return requestParameters;
    }
    public void setRequestParameters(LinkedList<Argument> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public LinkedList<Argument> getHeaders() {
        return headers;
    }

    public void setHeaders(LinkedList<Argument> headers) {
        this.headers = headers;
    }

    public LinkedList<Argument> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(LinkedList<Argument> queryParams) {
        this.queryParams = queryParams;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
