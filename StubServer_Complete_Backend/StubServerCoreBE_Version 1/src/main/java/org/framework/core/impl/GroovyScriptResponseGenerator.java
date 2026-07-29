package org.framework.core.impl;

import groovy.lang.Script;
import org.framework.core.ResponseGenerator;
import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.utils.Logger;
import java.util.HashMap;

public class GroovyScriptResponseGenerator implements ResponseGenerator {
    String script;
    Class<? extends Script> parsedScript;
    GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
    public GroovyScriptResponseGenerator(String script) {
        this.script = script;
        this.parsedScript = groovyExecutor.parseScript(script, script);
    }
    @Override
    public String GenerateResponse(Context context, MockRequest request) {
        // if (true){
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // throw new RuntimeException(e);
        // }
        // return null;
        // }

        if (script == null || script.isEmpty()) {
            return null;
        }
        try {
            // GroovyScriptExecutor groovyExecutor = new GroovyScriptExecutor();
            HashMap<String, Object> configureScriptProperties = new HashMap<>();
            configureScriptProperties.put("context", context);
            configureScriptProperties.put("mockOperation", context.getMockOperation());
            configureScriptProperties.put("mockRequest", request);
            configureScriptProperties.put("mockService", context.getMockService());
            String mockResponseName = groovyExecutor.runScript(parsedScript, configureScriptProperties);

            System.out.println("Returned mock response name from script " + mockResponseName);
            return mockResponseName;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getInstance().error(context.getMockService().getName(), e);
            return null;
        }
    }
}
