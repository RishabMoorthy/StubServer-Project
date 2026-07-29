package org.framework.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestTrackerDB {
    private static Map<String, EndpointsStats> serverLogs = new ConcurrentHashMap<>();
    private static RequestTrackerDB instance = new RequestTrackerDB();
    public static void logRequest(String name, RequestLog request){
        serverLogs.computeIfAbsent(name, k-> new EndpointsStats());
        EndpointsStats stats = serverLogs.get(name);
        // stats.incrementCount();
        stats.addLog(request);
        stats.addPort(request.getPort());
        stats.addServiceName(request.getServiceName());
    }

    public static void incrementRequest(String name){
        serverLogs.computeIfAbsent(name, k-> new EndpointsStats());
        EndpointsStats stats = serverLogs.get(name);
        stats.incrementCount();
    }

    public static RequestTrackerDB getInstance() {
        return instance;
    }

    public static void removeLogs(String name){
        if (serverLogs.containsKey(name))
            serverLogs.remove(name);
    }

    public static EndpointsStats getAllLogs(String name){
        return serverLogs.get(name);
    }

    public static Map<String, EndpointsStats> getAllServicesLogs(){
        return serverLogs;
    }
}
