package org.framework.core;

import org.framework.config.ServiceConfig;
import org.framework.services.rest.RestService;
import org.framework.services.soap.SoapService;
import org.framework.services.tcp.TCPService;

import java.io.IOException;

public class AbstractServiceFactory {
    public static AbstractService getInstance(ServiceConfig config) throws IOException {
        AbstractService service = null;
        if ("Rest".equals(config.getType())) {
            service = new RestService(config);
        } else if ("Soap".equals(config.getType())) {
            service = new SoapService(config);
        }
        else if ("Tcp".equals(config.getType())) {
            service = new TCPService(config);
        }
        return service;
    }
}
