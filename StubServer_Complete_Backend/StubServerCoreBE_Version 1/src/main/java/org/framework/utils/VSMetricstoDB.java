package org.framework.utils;

public class VSMetricstoDB {
    public void saveMetrics(String vsname, String virtServer){
        Logger.getInstance().info("Skipped Daily transaction metrics for "+vsname+" in "+virtServer);
    }
    public void saveMetricsForPerf(String vsname, String virtServer, String totalmet, String savedate){
        Logger.getInstance().info("Skipped Perf transaction metrics for "+vsname+" in "+virtServer);
    }

    public int checkCount(String vsname, String virtServer){
        System.out.println("I am in check count");
        Logger.getInstance().info("Skipped check count "+vsname+" in "+virtServer);
        return 0;
    }
}
