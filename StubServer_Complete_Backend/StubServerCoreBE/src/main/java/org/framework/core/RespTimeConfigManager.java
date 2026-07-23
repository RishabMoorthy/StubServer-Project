package org.framework.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RespTimeConfigManager {
    private static final Map<String, String> serviceRespTimeMap = new ConcurrentHashMap<>();

    public static Map<String, String> getConfig(){
        return serviceRespTimeMap;
    }
    public static void updateRespTimeConfig(String serviceName, String saveRespTime){
        serviceRespTimeMap.put(serviceName, saveRespTime);
    }
}
