package com.stubio.util;

import java.util.LinkedList;

public class RRPair {
    private String id, defaultResponse;
    private Request request;
    private ResponseSelection responseSelection = new ResponseSelection();
    private LinkedList<Response> responseSet;
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public ResponseSelection getResponseSelection() {
        return responseSelection;
    }

    public void setResponseSelection(ResponseSelection responseSelection) {
        this.responseSelection = responseSelection;
    }

    public LinkedList<Response> getResponseSet() {
        return responseSet;
    }

    public void setResponseSet(LinkedList<Response> responseSet) {
        this.responseSet = responseSet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultResponse() {
        return defaultResponse;
    }

    public void setDefaultResponse(String defaultResponse) {
        this.defaultResponse = defaultResponse;
    }
}
