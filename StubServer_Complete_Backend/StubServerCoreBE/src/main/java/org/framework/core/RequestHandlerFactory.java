package org.framework.core;

import org.framework.config.ServiceConfig;
import org.framework.core.impl.MockRequestHandler;
import org.framework.core.impl.TcpRequestHandler;
import org.framework.services.soap.SoapRequestHandler;

import java.io.IOException;

public class RequestHandlerFactory {
    public static RequestHandler getInstance(ServiceConfig config) throws IOException {
        RequestHandler handler = null;
        if (config.getType().equals("Rest")) {
            handler = new MockRequestHandler(config.getRoutes());
        } else if (config.getType().equals("Soap")) {
            handler = new SoapRequestHandler(config.getRoutes());
        }
        else if (config.getType().equals("Tcp")) {
            handler = new TcpRequestHandler(config.getRoutes());
        }

        return handler;
    }
}
