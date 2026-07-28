package com.stubio.parsers;

import com.stubio.util.Argument;
import com.stubio.util.Request;
import org.w3c.dom.Element;
import java.util.LinkedList;

public final class RequestParser {

    private RequestParser() {}

    public static Request parse(Element node) {

        Request request = new Request();

        request.setContentType(
                node.getAttribute("contentType"));

        for (Element child :
                XmlUtils.childElements(node)) {

            switch (XmlUtils.local(child)) {

                case "RequestData" ->
                        request.setRequestData(
                                XmlUtils.text(child));

                case "RequestParameters" ->
                        request.setRequestParameters(parseArguments(child));

                case "Headers" ->
                        request.setHeaders(
                                parseArguments(child));

                case "QueryParams" ->
                        request.setQueryParams(
                                parseArguments(child));
            }
        }

        return request;
    }

    private static LinkedList<Argument>
    parseArguments(Element parent) {

        LinkedList<Argument> args =
                new LinkedList<>();

        for (Element argNode :
                XmlUtils.childElements(parent)) {

            Argument argument =
                    new Argument();

            argument.setName(
                    argNode.getAttribute("name"));

            argument.setMatchType(
                    argNode.getAttribute("matchType"));

            argument.setCaseSensitive(
                    argNode.getAttribute("case"));

            argument.setEchoValue(
                    argNode.getAttribute("echoValue"));

            argument.setValue(
                    argNode.getTextContent());

            args.add(argument);
        }

        return args;
    }
}
