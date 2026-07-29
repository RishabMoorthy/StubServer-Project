package org.framework.core.impl;

import groovy.lang.*;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.framework.constants.PathConstants;
import org.framework.properties.Context;
import org.framework.utils.GlobalProperties;
import org.framework.utils.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GroovyScriptExecutor {

    private static final CompilerConfiguration config = new CompilerConfiguration();
    private static final GroovyClassLoader groovyClassLoader;
    private static final ConcurrentMap<String, Class<? extends Script>> scriptCache = new ConcurrentHashMap<>();
    private final Object compileLock = new Object();

    static {
        config.setClasspath(new File(PathConstants.CUSTOM_JAR_PATH).getPath());
        groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);

        try {
            // Preload classes once
            groovyClassLoader.loadClass("org.framework.utils.GroovyUtils");
            groovyClassLoader.loadClass("org.framework.utils.VSMetricsResptoDB");
            groovyClassLoader.loadClass("org.framework.utils.WrappedXmlHolder");
            groovyClassLoader.loadClass("org.framework.utils.VSMetricstoDB");

            // Add classpaths once
            groovyClassLoader.addClasspath(new File(PathConstants.VS_METRICS_TO_DB_JAR_PATH).getPath());
            groovyClassLoader.addClasspath(new File(PathConstants.EXECUTOR_GROOVY_PATH).getPath());
            groovyClassLoader.addClasspath(new File(PathConstants.HTTP_EXECUTOR_GROOVY_PATH).getPath());
            groovyClassLoader.addClasspath(new File(PathConstants.TCP_EXECUTOR_GROOVY_PATH).getPath());
            groovyClassLoader.addClasspath(new File(PathConstants.LIVE_RESPONSE_JAR_PATH).getPath());
            groovyClassLoader.addClasspath(new File(PathConstants.TRA_JAR_PATH).getPath());
            groovyClassLoader.addClasspath(new File(PathConstants.VS_RESPONSE_TO_DB_JAR_PATH).getPath());
        } catch (ClassNotFoundException e) {
            Logger.getInstance().error("Error loading Groovy classes", e);
        }
    }

    /**
     * Parses and caches the script for reuse.
     */
    public Class<? extends Script> parseScript(String cacheKey, String scriptText) {
        return scriptCache.computeIfAbsent(cacheKey, k -> {
            // GroovyClassLoader.parseClass is not guaranteed to be thread-safe
            synchronized (compileLock) {
                return groovyClassLoader.parseClass(scriptText);
            }
        });
    }

    /**
     * Runs a pre-parsed script with given properties.
     */
    public String runScript(Class<? extends Script> cls, HashMap<String, Object> properties) {
        if (cls == null)
            return null;
        try {
            Binding binding = new Binding();
            if (properties != null) {
                for (Map.Entry<String, Object> property : properties.entrySet()) {
                    binding.setVariable(property.getKey(), property.getValue());
                }
            }
            binding.setVariable("log", Logger.getInstance());
            binding.setVariable("globalProperties", GlobalProperties.getInstance());
            Script script = InvokerHelper.createScript(cls, binding);

            Object result = script.run();
            return (result instanceof String) ? (String) result : String.valueOf(result);
        } catch (Exception e) {
            // Logger.getInstance().error("Error running Groovy script", e);
            throw e;
            // return null;
        }
    }

    /**
     * Executes a script directly (parse + run) for cases where caching isn't used.
     */
    public String executeScript(String scriptText, HashMap<String, Object> properties, Context context) {
        if (scriptText == null || scriptText.isEmpty()) {
            return null;
        }
        try {
            Class<? extends Script> script = parseScript(scriptText, scriptText); // Uses cache
            return runScript(script, properties);
        } catch (Exception e) {
            // Logger.getInstance().error(e, context);
            throw e;
        }
    }
}
