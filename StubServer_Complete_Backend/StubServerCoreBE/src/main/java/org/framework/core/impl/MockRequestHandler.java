package org.framework.core.impl;

import com.stubio.util.Endpoint;

import com.sun.net.httpserver.Headers;

import org.framework.core.RequestHandler;
import org.framework.core.ResponseResolver;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.properties.MockResponse;
import org.framework.utils.Logger;

import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST dispatch over the com.stubio model.
 *
 * <p>
 * Path templating and method matching moved here verbatim from RestRoute,
 * because Endpoint is a plain data holder and carries no behaviour.
 */
public class MockRequestHandler implements RequestHandler {

    private final List<Endpoint> endpoints;
    private final ResponseResolver resolver;

    /** Pre-split path templates, keyed by endpoint - was RestRoute.pathTemplateParts. */
    private final Map<Endpoint, String[]> pathTemplates = new IdentityHashMap<>();

    public MockRequestHandler(List<Endpoint> endpoints, ResponseResolver resolver) {
        this.endpoints = endpoints;
        this.resolver = resolver;

        for (Endpoint endpoint : endpoints) {
            String path = endpoint.getPath() == null ? "" : endpoint.getPath();
            pathTemplates.put(endpoint, path.split("/"));
        }
    }

    @Override
    public MockResponse handleRequest(Context context, MockRequest request) throws Exception {

        String method = request.getMethod();
        String path = request.getPath();

        Logger.getInstance().info("Method : " + method);
        Logger.getInstance().info("Path : " + path);

        for (Endpoint endpoint : endpoints) {
            if (matchRequest(endpoint, context, request)) {
                System.out.println("request matched");
                return resolver.getResponse(endpoint, context, request);
            }
        }

        Headers headers = new Headers();
        headers.add("content-type", "plain/text");
        String out = "The requested resource could not be found on the server";

        // if no endpoint matched
        return new MockResponse("404 Not Found", headers, 404, out.getBytes(StandardCharsets.UTF_8), "", "", "");
    }

    /** Was RestRoute.matchRequest(). */
    private boolean matchRequest(Endpoint endpoint, Context context, MockRequest request) {
        boolean matchRequestPath = matchPath(endpoint, request);
        boolean matchRequestMethod = matchMethod(endpoint, request);
        boolean hasMatch = matchRequestMethod && matchRequestPath;
        if (hasMatch) {
            updateParams(endpoint, context, request);
        }
        return hasMatch;

    }

    /** Was RestRoute.matchPath(). */
    private boolean matchPath(Endpoint endpoint, MockRequest request) {
        String[] pathTemplateParts = pathTemplates.get(endpoint);
        String[] requestPathParts = request.getPath().split("/");
        if (pathTemplateParts.length != requestPathParts.length) {
            return false;
        }
        for (int i = 0; i < pathTemplateParts.length; i++) {
            if (pathTemplateParts[i].contains("{")) {
                continue;
            } else if (!pathTemplateParts[i].equals(requestPathParts[i])) {
                return false;
            }
        }
        return true;
    }

    /** Was RestRoute.matchMethod(). */
    private boolean matchMethod(Endpoint endpoint, MockRequest request) {
        return request.getMethod().equals(endpoint.getMethod());
    }

    /** Was RestRoute.updateParams() - captures {var} path segments into the context. */
    private void updateParams(Endpoint endpoint, Context context, MockRequest request) {
        String[] pathTemplateParts = pathTemplates.get(endpoint);
        String[] requestPathParts = request.getPath().split("/");
        for (int i = 0; i < pathTemplateParts.length; i++) {
            if (pathTemplateParts[i].contains("{")) {
                String variableName = pathTemplateParts[i].substring(1, pathTemplateParts[i].length() - 1);
                String value = requestPathParts[i];
                context.getDynamicProperties().put(variableName, value);
            }
        }
    }
}
