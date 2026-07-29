package org.framework.config;

import org.framework.core.ServerManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class LogConfigManager {
    private static final Map<String, String> serviceLogMap = new ConcurrentHashMap<>();

    private static int days = 0;

    public static Map<String, String> getConfig() {
        return serviceLogMap;
    }

    // Update log setting for a specific service
    public static void updateServiceLog(String serviceName, String saveLog, int NoOfdays) {
        if (NoOfdays != 0)
            days = NoOfdays;
        if (!(serviceName == null || serviceName.isEmpty()))
            if ("All".equalsIgnoreCase(serviceName)) {
                // Apply to all known services
                for (String service : ServerManager.getInstance().getServices().keySet()) {
                    serviceLogMap.put(service, saveLog);
                }
            } else {
                serviceLogMap.put(serviceName, saveLog);
            }
    }

    // Check if logging is enabled for a service
    public static String shouldLog(String serviceName) {
        return serviceLogMap.getOrDefault(serviceName, "No");
    }

    public static int getDays() {
        return days;
    }

    public static void setDays(int days) {
        LogConfigManager.days = days;
    }
}
