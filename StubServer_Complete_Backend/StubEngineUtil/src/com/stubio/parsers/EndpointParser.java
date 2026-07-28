package com.stubio.parsers;

import com.stubio.util.Endpoint;
import com.stubio.util.VirtualServiceObject;
import org.w3c.dom.Element;

import java.util.LinkedList;

public final class EndpointParser {

    public static LinkedList<Endpoint> parse(
            Element endpointsNode,
            VirtualServiceObject vso) {

        LinkedList<Endpoint> endpoints = new LinkedList<>();

        for (Element endpointNode : XmlUtils.childElements(endpointsNode)) {

            Endpoint endpoint = new Endpoint();

            endpoint.setName(
                    endpointNode.getAttribute("name"));

            endpoint.setPath(
                    endpointNode.getAttribute("path"));

            endpoint.setMethod(
                    endpointNode.getAttribute("method"));

            endpoint.setDefaultRR(
                    endpointNode.getAttribute("defaultRR"));

            for (Element child : XmlUtils.childElements(endpointNode)) {

                switch (XmlUtils.local(child)) {

                    case "RequestData" ->
                            endpoint.setRequestList(
                                    RequestDataParser.parse(child));

                    case "Filters" ->
                            endpoint.setFilterList(
                                    FilterParser.parse(child));

                    case "DataGenerators" ->
                            endpoint.setDataGenerators(
                                    DataGeneratorParser.parse(child));

                    case "DataSourceSelect" ->
                            endpoint.setDataSourceSelect(
                                    DataSourceSelectParser.parse(
                                            child,
                                            endpoint,
                                            vso));

                    case "RRPair" ->
                            endpoint.getRrList()
                                    .add(RRPairParser.parse(child));
                }
            }

            endpoints.add(endpoint);
        }

        return endpoints;
    }
}
