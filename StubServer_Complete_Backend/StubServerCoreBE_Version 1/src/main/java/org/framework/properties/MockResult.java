package org.framework.properties;

import java.nio.charset.StandardCharsets;

public class MockResult {
    MockRequest mockRequest;
    MockResponse mockResponse;
    String responseContent;

    public MockResult(MockRequest request, MockResponse response) {
        this.mockRequest = request;
        this.mockResponse = response;
        this.responseContent = (new String(response.getResponseBytes(), StandardCharsets.UTF_8));
    }

    public MockRequest getMockRequest() {
        return mockRequest;
    }

    public void setMockRequest(MockRequest mockRequest) {
        this.mockRequest = mockRequest;
    }

    public MockResponse getMockResponse() {
        return mockResponse;
    }

    public void setMockResponse(MockResponse mockResponse) {
        this.mockResponse = mockResponse;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }
}
