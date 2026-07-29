package org.framework.core.impl;

import java.util.concurrent.ConcurrentHashMap;

public class DelayRegistry {

    private static final ConcurrentHashMap<String, DelayContext> contexts
            = new ConcurrentHashMap<>();

    public static DelayContext getContext(String serviceName) {
        return contexts.computeIfAbsent(serviceName, k -> new DelayContext());
    }
}
