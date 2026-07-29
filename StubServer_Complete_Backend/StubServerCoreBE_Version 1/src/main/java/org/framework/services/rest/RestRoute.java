package org.framework.services.rest;

import org.framework.properties.Context;
import org.framework.properties.MockResponse;
import org.framework.core.BaseRoute;
import org.framework.core.ResponseGenerator;
import org.framework.properties.MockRequest;

import java.util.List;

public class RestRoute extends BaseRoute {

    String method;

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    String path;
    String[] pathTemplateParts;

    public RestRoute(String serviceName, ResponseGenerator generator, List<MockResponse> responses,
            String defaultResponse, String path, String method, String name, String dispatchStyle, String contentType) {
        super(serviceName, generator, responses, defaultResponse, name, dispatchStyle, contentType);
        this.method = method;
        this.path = path;
        this.pathTemplateParts = path.split("/");
    }

    @Override
    public boolean matchRequest(Context context, MockRequest request) {
        boolean matchRequestPath = this.matchPath(request);
        boolean matchRequestMethod = this.matchMethod(request);
        boolean hasMatch = matchRequestMethod && matchRequestPath;
        if (hasMatch) {
            updateParams(context, request);
        }
        return hasMatch;
    }

    public void updateParams(Context context, MockRequest request) {
        String[] requestPathParts = request.getPath().split("/");
        for (int i = 0; i < pathTemplateParts.length; i++) {
            if (pathTemplateParts[i].contains("{")) {
                String variableName = pathTemplateParts[i].substring(1, pathTemplateParts[i].length() - 1);
                String value = requestPathParts[i];
                context.getDynamicProperties().put(variableName, value);
            }
        }
    }

    public MockResponse getMockResponseByName(String mockResponseName) {
        List<MockResponse> mockResponses = super.getResponses();
        for (int i = 0; i < mockResponses.size(); i++) {
            if (mockResponses.get(i).getName().equals(mockResponseName)) {
                return mockResponses.get(i);
            }
        }

        return null;
    }

    public boolean matchPath(MockRequest request) {
        String[] requestPathParts = request.getPath().split("/");
        if (pathTemplateParts.length != requestPathParts.length)
            return false;
        for (int i = 0; i < pathTemplateParts.length; i++) {
            if (pathTemplateParts[i].contains("{")) {
                continue;
            } else if (!pathTemplateParts[i].equals(requestPathParts[i])) {
                return false;
            }
        }
        // extract param
        return true;
    }

    public boolean matchMethod(MockRequest request) {
        return request.getMethod().equals(method);
    }
}
