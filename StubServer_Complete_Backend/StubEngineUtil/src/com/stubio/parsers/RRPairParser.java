package com.stubio.parsers;

import com.stubio.util.RRPair;
import org.w3c.dom.Element;

public final class RRPairParser {

    public static RRPair parse(Element node) {

        RRPair pair = new RRPair();

        pair.setId(node.getAttribute("id"));
        pair.setDefaultResponse(
                node.getAttribute("defaultResponse"));

        for (Element child :
                XmlUtils.childElements(node)) {

            switch (XmlUtils.local(child)) {

                case "Request" ->
                        pair.setRequest(
                                RequestParser.parse(child));

                case "ResponseSelection" ->
                        pair.setResponseSelection(
                                ResponseSelectionParser.parse(child));

                case "ResponseSet" ->
                        pair.setResponseSet(
                                ResponseParser.parse(child));
            }
        }

        return pair;
    }
}
