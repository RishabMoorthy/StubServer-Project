package org.framework.core.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockResponseProcessor {
    private final PropertyResolver propertyResolver;

    public MockResponseProcessor(PropertyResolver resolver) {
        this.propertyResolver = resolver;
    }

    public String processMockResponse(String responseTemplate) {
        try {
            // Pattern to match placeholders like ${#MockService#propertyName}
            Pattern pattern = Pattern.compile("\\$\\{#?([^#]+)#?([a-zA-Z0-9_]+)}");
            Matcher matcher = pattern.matcher(responseTemplate);
            StringBuffer resolvedResponse = new StringBuffer();

            while (matcher.find()) {
                String placeholder = matcher.group(1); // Extract the content inside ${...}
                String resolvedValue = propertyResolver.resolveProperty(placeholder);
                matcher.appendReplacement(resolvedResponse, resolvedValue);
            }
            matcher.appendTail(resolvedResponse);
            return resolvedResponse.toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
