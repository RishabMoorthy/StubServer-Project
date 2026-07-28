package com.stubio.util;

import java.util.LinkedList;

public class DefaultErrorResponse {

    String response, statusCode, statusMessage, contentType, responseDelay;
    LinkedList<GenericProperty> headerList = new LinkedList<>();

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getResponseDelay() {
        return responseDelay;
    }

    public void setResponseDelay(String responseDelay) {
        this.responseDelay = responseDelay;
    }

    public LinkedList<GenericProperty> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(LinkedList<GenericProperty> headerList) {
        this.headerList = headerList;
    }
}
