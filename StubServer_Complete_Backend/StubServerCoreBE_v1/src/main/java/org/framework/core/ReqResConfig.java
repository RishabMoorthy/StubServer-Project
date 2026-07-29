package org.framework.core;

public class ReqResConfig {
    private String serviceName;
    private int keepDataFor;
    private String saveLog;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getKeepDataFor() {
        return keepDataFor;
    }

    public void setKeepDataFor(int keepDataFor) {
        this.keepDataFor = keepDataFor;
    }

    public String isSaveLog() {
        return saveLog;
    }

    public void setSaveLog(String saveLog) {
        this.saveLog = saveLog;
    }

    // Getters and setters
}
