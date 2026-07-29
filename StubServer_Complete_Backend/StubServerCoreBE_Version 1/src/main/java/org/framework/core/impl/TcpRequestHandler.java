package org.framework.core.impl;

import com.sun.net.httpserver.Headers;
import org.framework.core.BaseRoute;
import org.framework.core.RequestHandler;
import org.framework.properties.Buffer;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.properties.MockResponse;
import org.framework.utils.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class TcpRequestHandler implements RequestHandler {

    private final List<BaseRoute> routes;

    public TcpRequestHandler(List<BaseRoute> routes) {
        this.routes = routes;
    }

    @Override
    public MockResponse handleRequest(Context context, MockRequest request) throws Exception {

        String operation = request.getOperation();
        Logger.getInstance().info("Operation : " + operation);

        for (BaseRoute route : routes) {
            if (route.matchRequest(context, request)) {
                System.out.println("request matched");
                return route.getResponse(context, request);
            }
        }

        // if no route matched
        return new MockResponse("404 Not Found".getBytes());
    }
}
