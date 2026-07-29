package org.framework.core;

import com.stubio.util.Endpoint;
import com.stubio.util.StubOperation;

import org.framework.config.ServiceConfig;
import org.framework.core.impl.MockRequestHandler;
import org.framework.core.impl.TcpRequestHandler;
import org.framework.services.soap.SoapRequestHandler;

import java.io.IOException;
import java.util.List;

public class RequestHandlerFactory {

    /**
     * REST and SOAP dispatch over the com.stubio model (Endpoint /
     * StubOperation), sharing a ResponseResolver that carries the response
     * selection logic BaseRoute used to own.
     */
    public static RequestHandler getInstance(ServiceConfig config) throws IOException {
        RequestHandler handler = null;

        if ("Rest".equals(config.getType())) {
            List<Endpoint> endpoints = config.getEndpoints();
            ResponseResolver resolver = new ResponseResolver(config.getServiceName());
            endpoints.forEach(resolver::register);
            handler = new MockRequestHandler(endpoints, resolver);

        } else if ("Soap".equals(config.getType())) {
            List<StubOperation> operations = config.getStubOperations();
            ResponseResolver resolver = new ResponseResolver(config.getServiceName());
            operations.forEach(resolver::register);
            handler = new SoapRequestHandler(operations, resolver);

        } else if ("Tcp".equals(config.getType())) {
            handler = new TcpRequestHandler(config.getRoutes());
        }

        return handler;
    }
}
