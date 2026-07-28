package com.stubio.parsers;

import com.stubio.util.Script;
import org.w3c.dom.Element;

public final class ScriptParser {

    public ScriptParser() {
    }

    public static Script parse(Element node) {

        Script script = new Script();

        for (Element child : XmlUtils.childElements(node)) {

            switch (XmlUtils.local(child)) {

                case "ScriptLang" ->
                        script.setScriptLanguage(
                                XmlUtils.text(child));

                case "Script" ->
                        script.setScript(
                                XmlUtils.text(child));

                case "ExecutionType" ->
                        script.setScriptType(
                                XmlUtils.text(child));
            }
        }

        return script;
    }
}
