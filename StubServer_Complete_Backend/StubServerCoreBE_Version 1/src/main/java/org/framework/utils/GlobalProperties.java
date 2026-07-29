package org.framework.utils;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalProperties {
    private static final ConcurrentHashMap<String, String> globalProperties = new ConcurrentHashMap<>();

    public static void setPropertyValue(String key, String value) {
        GlobalProperties.globalProperties.put(key, value);
    }

    private static final GlobalProperties instance = new GlobalProperties();

    public static GlobalProperties getInstance() {
        return instance;
    }

    public static String getPropertyValue(String key) {
        return GlobalProperties.globalProperties.get(key);
    }
}
