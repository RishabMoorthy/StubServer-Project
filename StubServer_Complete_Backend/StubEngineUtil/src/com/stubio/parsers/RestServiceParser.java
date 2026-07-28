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
        vso.setPort(
                Integer.parseInt(
                        node.getAttribute("port")));

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
