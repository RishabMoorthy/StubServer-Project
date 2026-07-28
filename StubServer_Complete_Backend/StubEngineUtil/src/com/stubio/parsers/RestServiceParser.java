package com.stubio.parsers;
import com.stubio.util.Endpoint;
import com.stubio.util.RestService;
import com.stubio.util.VirtualServiceObject;
import org.w3c.dom.Element;
import java.util.LinkedList;

public final class RestServiceParser {
    public static RestService parse(
            Element node,
            VirtualServiceObject vso) {

        RestService service = new RestService();
        vso.setVsType("rest");

        // VirtualServiceMapper has already parsed this attribute safely; guard
        // here too so a service without a port attribute does not throw.
        String port = node.getAttribute("port");
        if (!port.isBlank()) {
            try {
                vso.setPort(Integer.parseInt(port.trim()));
            } catch (NumberFormatException ignored) {
            }
        }

        vso.setHost(
                node.getAttribute("host"));

        vso.setContextPath(
                node.getAttribute("contextPath"));

        LinkedList<Endpoint> endpoints = new LinkedList<>();

        for (Element child : XmlUtils.childElements(node)) {

            switch (XmlUtils.local(child)) {

                case "SecuritySettings" ->
                        service.setSecuritySettings(
                                SecuritySettingsParser.parse(
                                        child));

                case "Endpoints" ->
                        endpoints.addAll(
                                EndpointParser.parse(
                                        child, vso));
            }
        }

        service.setEndpoints(endpoints);

        return service;
    }
}
