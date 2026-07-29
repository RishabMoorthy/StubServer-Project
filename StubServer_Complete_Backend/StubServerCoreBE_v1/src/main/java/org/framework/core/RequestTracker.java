package org.framework.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestTracker {
    private static Map<String, EndpointsStats> serverLogs = new ConcurrentHashMap<>();
    private static RequestTracker instance = new RequestTracker();
    public static void logRequest(String name, RequestLog request){
        serverLogs.computeIfAbsent(name, k-> new EndpointsStats());
        EndpointsStats stats = serverLogs.get(name);

        stats.addLog(request);
        stats.addServiceName(request.getServiceName());
        stats.addPort(request.getPort());
        stats.setStatus(request.getStatus());
        stats.setSource(request.getResponse().getSource());
        stats.setType(request.getType());
    }

    public static void incrementRequest(String name){
        serverLogs.computeIfAbsent(name, k-> new EndpointsStats());
        EndpointsStats stats = serverLogs.get(name);
        stats.incrementCount();
    }

    public static RequestTracker getInstance() {
        return instance;
    }

    public static void removeLogs(String name){
        if (serverLogs.containsKey(name))
            serverLogs.remove(name);
    }

    public static EndpointsStats getAllLogs(String name){
        return serverLogs.get(name);
    }
}
