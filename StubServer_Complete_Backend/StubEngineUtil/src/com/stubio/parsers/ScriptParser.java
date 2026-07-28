package com.stubio.parsers;

import com.stubio.util.Script;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;

public final class ScriptParser {

    public ScriptParser() {
    }

    /**
     * Parses a CustomScripts block into a map keyed by ExecutionType
     * ("VS Start", "VS Stop", "On Request", "On Response").
     *
     * <p>
     * Shared by VirtualServiceMapper (root-level CustomScripts, as used by
     * RestService) and SoapServiceParser (nested inside SOAPService).
     */
    public static LinkedHashMap<String, Script> parseCustomScripts(
            Element customScriptsNode) {

        LinkedHashMap<String, Script> scripts =
                new LinkedHashMap<>();

        for (Element scriptNode :
                XmlUtils.childElements(customScriptsNode)) {

            if (!"CustomScript".equals(
                    XmlUtils.local(scriptNode))) {
                continue;
            }

            Script script = parse(scriptNode);

            if (script.getScriptType() != null
                    && !script.getScriptType().isBlank()) {

                scripts.put(
                        script.getScriptType(),
                        script);
            }
        }

        return scripts;
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
