package com.stubio.parsers;

import com.stubio.util.ResponseSelection;
import org.w3c.dom.Element;


public final class ResponseSelectionParser {

    private ResponseSelectionParser() {
    }

    public static ResponseSelection parse(Element node) {

        ResponseSelection responseSelection =
                new ResponseSelection();

        for (Element child : XmlUtils.childElements(node)) {

            switch (XmlUtils.local(child)) {

                case "MatchStyle" ->
                        responseSelection.setMatchStyle(
                                XmlUtils.text(child));

                case "MatchScript" ->
                        responseSelection.setMatchScript(
                                ScriptParser.parse(child));
            }
        }

        return responseSelection;
    }
}
