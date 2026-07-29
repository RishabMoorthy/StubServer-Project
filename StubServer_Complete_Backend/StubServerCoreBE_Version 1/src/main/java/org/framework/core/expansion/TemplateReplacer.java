package org.framework.core.expansion;

import java.util.HashMap;
import java.util.Map;

public class TemplateReplacer {

    Map<String, Map<String, Object>> properties = new HashMap<>();

    public void addPropertyMap(String name, Map<String, Object> properties) {
        this.properties.put(name, properties);
    }

    public String replaceTemplate(String template) {
        try {
            for (Map.Entry<String, Map<String, Object>> property : this.properties.entrySet()) {
                for (Map.Entry<String, Object> value : property.getValue().entrySet()) {
                    String key = "#" + property.getKey();
                    String secondKey = "#" + value.getKey();
                    if (property.getKey().isEmpty()) {
                        key = "";
                        secondKey = value.getKey();
                    }

                    template = template.replace("${" + key + secondKey + "}",
                            value.getValue() != null ? value.getValue().toString() : "");
                    template = template.replace("${" + key + secondKey + "#$}",
                            value.getValue() != null ? value.getValue().toString() : "");
                    // System.out.println("template "+template);
                }
            }
            // regex to find and replace all the templates with ""
            template = removeDynamicTemplates(template);
            return template;
        } catch (Exception e) {
            System.out.print("exception " + e);
        }

        return template;
    }

    private String removeDynamicTemplates(String body) {

        // ${ ... }
        body = body.replaceAll("\\$\\{[^}]*}", "");

        // #MockService#anything
        body = body.replaceAll("#MockService#[^<\\s]+", "");

        // #MockResponse#Request#anything
        body = body.replaceAll("#MockResponse#Request#[^<\\s]+", "");

        return body;
    }
}
