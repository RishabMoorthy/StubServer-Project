package com.stubio.parsers;

import com.stubio.util.GenericProperty;
import com.stubio.util.Response;
import org.w3c.dom.Element;

import java.util.LinkedList;

public final class ResponseParser {

    private ResponseParser() {
    }

    public static LinkedList<Response> parse(
            Element responseSetNode) {

        LinkedList<Response> responses =
                new LinkedList<>();

        for (Element responseNode :
                XmlUtils.childElements(responseSetNode)) {

            if (!"Response".equals(
                    XmlUtils.local(responseNode))) {
                continue;
            }

            Response response =
                    new Response();

            response.setName(
                    responseNode.getAttribute("name"));

            response.setContentType(
                    responseNode.getAttribute("contentType"));

            response.setHttpMsg(
                    responseNode.getAttribute("httpMsg"));

            String statusCode =
                    responseNode.getAttribute("statusCode");

            if (!statusCode.isBlank()) {
                response.setStatusCode(
                        Integer.parseInt(statusCode));
            }

            String responseDelay =
                    responseNode.getAttribute("responseDelay");

            if (!responseDelay.isBlank()) {
                response.setResponseDelay(
                        Integer.parseInt(responseDelay));
            }

            for (Element child :
                    XmlUtils.childElements(responseNode)) {

                switch (XmlUtils.local(child)) {

                    case "Body" ->
                            response.setBody(
                                    child.getTextContent());

                    case "ResponseScript" ->
                            response.setResponseScript(
                                    ScriptParser.parse(child));

                    case "CustomHeaders" ->
                            response.setCustomHeaders(
                                    parseCustomHeaders(child));
                }
            }

            responses.add(response);
        }

        return responses;
    }

    private static LinkedList<GenericProperty>
    parseCustomHeaders(
            Element headersNode) {

        LinkedList<GenericProperty> headers =
                new LinkedList<>();

        for (Element headerNode :
                XmlUtils.childElements(headersNode)) {

            if (!"Header".equals(
                    XmlUtils.local(headerNode))) {
                continue;
            }

            GenericProperty property =
                    new GenericProperty();

            for (Element field :
                    XmlUtils.childElements(headerNode)) {

                // The SOAP sample writes <vs:key> in lower case, REST writes
                // <vs:Key>; accept either so header names are never dropped.
                switch (XmlUtils.local(field)) {

                    case "Key", "key" ->
                            property.setKey(
                                    XmlUtils.text(field));

                    case "Value", "value" ->
                            property.setValue(
                                    XmlUtils.text(field));
                }
            }

            headers.add(property);
        }

        return headers;
    }
}
