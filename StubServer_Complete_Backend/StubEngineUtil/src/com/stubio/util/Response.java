package com.stubio.util;

import java.util.LinkedList;

public class Response {

    private String name;
    private int statusCode;
    private String httpMsg;
    private String contentType;
    private int responseDelay;
    private Boolean defaultResponse; // attribute "default" may be absent

    private String body; // <vs:Body>
    private Script responseScript; // <vs:ResponseScript>

    public Response() {

    }

    public Response(Response response) {
        this.name = response.getName();
        this.statusCode = response.getStatusCode();
        this.contentType = response.getContentType();
        this.httpMsg = response.getHttpMsg();
        this.responseDelay = response.getResponseDelay();
        this.defaultResponse = response.getDefaultResponse();
        this.body = response.getBody();
        this.responseScript = response.getResponseScript();
        this.customHeaders = response.getCustomHeaders();
    }

    public Response(String name, int statusCode, String contentType, String httpMsg, int responseDelay,
            Boolean defaultResponse, String body, Script responseScript, LinkedList<GenericProperty> customHeaders) {
        this.name = name;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.httpMsg = httpMsg;
        this.responseDelay = responseDelay;
        this.defaultResponse = defaultResponse;
        this.body = body;
        this.responseScript = responseScript;
        this.customHeaders = customHeaders;
    }

    private LinkedList<GenericProperty> customHeaders; // <vs:CustomHeaders>

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getHttpMsg() {
        return httpMsg;
    }

    public void setHttpMsg(String httpMsg) {
        this.httpMsg = httpMsg;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getResponseDelay() {
        return responseDelay;
    }

    public void setResponseDelay(int responseDelay) {
        this.responseDelay = responseDelay;
    }

    public Boolean getDefaultResponse() {
        return defaultResponse;
    }

    public void setDefaultResponse(Boolean defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Script getResponseScript() {
        return responseScript;
    }

    public void setResponseScript(Script responseScript) {
        this.responseScript = responseScript;
    }

    public LinkedList<GenericProperty> getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(LinkedList<GenericProperty> customHeaders) {
        this.customHeaders = customHeaders;
    }
}
