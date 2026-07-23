package org.framework.utils;

public class VSMetricsResptoDB {
    public String saveRespMetrics(String vsname, String virtServer, String startdt, String enddt,
            int count, long maxresptime) {
        Logger.getInstance().info("Skipped saving resp time for perf transaction for " + vsname + " in " + virtServer);
        return "Success";
    }
}
