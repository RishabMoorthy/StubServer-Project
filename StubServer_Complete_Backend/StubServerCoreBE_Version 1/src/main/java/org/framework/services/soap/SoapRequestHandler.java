package org.framework.services.soap;

import com.sun.net.httpserver.Headers;
import org.framework.core.BaseRoute;
import org.framework.core.RequestHandler;
import org.framework.core.impl.MockRequestHandler;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.properties.MockResponse;
import org.framework.utils.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SoapRequestHandler implements RequestHandler {
    private final List<BaseRoute> routes;

    public SoapRequestHandler(List<BaseRoute> routes) {
        this.routes = routes;
    }

    @Override
    public MockResponse handleRequest(Context context, MockRequest request) throws Exception {
        String operation = request.getOperation();
        String soap_interface = request.getSoap_interface();

        Logger.getInstance().info("operation : " + operation);
        Logger.getInstance().info("soap_interface : " + soap_interface);

        for (BaseRoute route : routes) {
            if (route.matchRequest(context, request)) {
                System.out.println("request matched");
                return route.getResponse(context, request);
            }
        }

        Headers headers = new Headers();
        headers.add("content-type", "plain/text");
        String out = "Page not found";

        // if no route matched
        return new MockResponse("defaultResponse", headers, 404,
                out.getBytes(StandardCharsets.UTF_8), "", "", "");
    }
}
