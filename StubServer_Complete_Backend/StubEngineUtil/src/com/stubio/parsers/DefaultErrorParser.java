package com.stubio.parsers;

import com.stubio.util.DefaultErrorResponse;
import com.stubio.util.GenericProperty;
import org.w3c.dom.Element;

import java.util.LinkedList;

public final class DefaultErrorParser {

    private DefaultErrorParser() {
    }

    public static DefaultErrorResponse parse(Element node) {

        DefaultErrorResponse response = new DefaultErrorResponse();

        for (Element child : XmlUtils.childElements(node)) {

            if (!"Response".equals(
                    XmlUtils.local(child))) {
                continue;
            }

            response.setStatusCode(
                    child.getAttribute("statusCode"));

            response.setStatusMessage(
                    child.getAttribute("statusMsg"));

            response.setContentType(
                    child.getAttribute("contentType"));

            response.setResponseDelay(
                    child.getAttribute("responseDelay"));

            for (Element respChild : XmlUtils.childElements(child)) {

                switch (XmlUtils.local(respChild)) {

                    case "body" ->
                            response.setResponse(
                                    respChild.getTextContent());

                    case "Headers" ->
                            response.setHeaderList(
                                    parseHeaders(respChild));
                }
            }
        }

        return response;
    }

    private static LinkedList<GenericProperty> parseHeaders(
            Element headersNode) {

        LinkedList<GenericProperty> headers = new LinkedList<>();

        for (Element headerNode : XmlUtils.childElements(headersNode)) {

            if (!"Header".equals(
                    XmlUtils.local(headerNode))) {
                continue;
            }

            GenericProperty property = new GenericProperty();

            for (Element child : XmlUtils.childElements(headerNode)) {

                switch (XmlUtils.local(child)) {

                    case "Key" ->
                            property.setKey(
                                    XmlUtils.text(child));

                    case "Value" ->
                            property.setValue(
                                    XmlUtils.text(child));
                }
            }

            headers.add(property);
        }

        return headers;
    }
}
