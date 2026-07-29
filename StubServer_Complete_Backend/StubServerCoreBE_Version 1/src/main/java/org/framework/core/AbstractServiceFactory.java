package org.framework.core;

import org.framework.config.ServiceConfig;
import org.framework.services.rest.RestService;
import org.framework.services.soap.SoapService;
import org.framework.services.tcp.TCPService;

import java.io.IOException;

public class AbstractServiceFactory {
    public static AbstractService getInstance(ServiceConfig config) throws IOException {
        AbstractService service = null;
        if (config.getType().equals("Rest")) {
            service = new RestService(config);
        } else if (config.getType().equals("Soap")) {
            service = new SoapService(config);
        }
        else if (config.getType().equals("Tcp")) {
            service = new TCPService(config);
        }
        return service;
    }
}
