package org.framework.services.rest;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class SimpleJettyServer {

    public static void main(String[] args) throws Exception {
        boolean clientAuth = false;
        boolean isSecured = true;
        Server server = new Server();
        if (!isSecured) {
            // -------- HTTP only --------
            HttpConfiguration httpConfig = new HttpConfiguration();
            ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
            httpConnector.setHost("10.39.39.222");
            httpConnector.setPort(8334);
            server.setConnectors(new Connector[] { httpConnector });
            System.out.println("Starting HTTP on " + "host" + ":" + "port");
        }

        else {
            // 1. Configure SSL (Load the keystore)
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

            sslContextFactory.setKeyStorePath(
                    "C:\\Users\\W0021871\\enhancement\\stubserver\\backend\\vsfiles\\canvmnp1was012.hqintl1.com.jks");
            sslContextFactory.setKeyStorePassword("Wusvdigital123*");
            sslContextFactory.setKeyStoreType("JKS");

            if (clientAuth) {

                sslContextFactory.setTrustStorePath("");
                sslContextFactory.setTrustStoreType("");
                sslContextFactory.setTrustStorePassword("JKS");
                // Require client certs
                sslContextFactory.setNeedClientAuth(true);
            }

            // 3. Configure the HTTPS Connector
            HttpConfiguration https_config = new HttpConfiguration();
            https_config.addCustomizer(new SecureRequestCustomizer()); // Adds SSL info to requests

            ServerConnector sslConnector = new ServerConnector(server,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(https_config));

            sslConnector.setPort(8443);
            server.setConnectors(new Connector[] { sslConnector });
        }

        // 4. Set the Handler (Your Logic)
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                    HttpServletResponse response) throws IOException {
                System.out.println("hello i came");

                response.setContentType("text/plain;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                response.getWriter().println("Hello from Secure Jetty!");
            }
        });

        // 5. Start
        server.start();
        System.out.println("Jetty Secure Server started on https://localhost:8443");
        server.join();
    }
}
