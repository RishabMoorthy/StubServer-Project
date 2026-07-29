package org.framework.core;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class EndpointsStats {
    private static final int MAX_LOGS = 50;
    private final AtomicLong requestCount;
    private ConcurrentLinkedQueue<RequestLog> listOfLogs;
    private String serviceName;
    private String status;
    private Integer port;
    private String type;
    private String source;
    private int count = 0;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public AtomicLong getRequestCount() {
        return requestCount;
    }

    public void addServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void addPort(Integer port) {
        this.port = port;
    }

    public ConcurrentLinkedQueue<RequestLog> getListOfLogs() {
        return listOfLogs;
    }

    public void setListOfLogs(ConcurrentLinkedQueue<RequestLog> listOfLogs) {
        this.listOfLogs = listOfLogs;
    }

    public EndpointsStats() {
        this.listOfLogs = new ConcurrentLinkedQueue<>();
        this.requestCount = new AtomicLong(0);
    }

    public void incrementCount() {
        requestCount.incrementAndGet();
    }

    public void addLog(RequestLog log) {
        // System.out.println("log "+log.request.getMethod());
        listOfLogs.add(log);

        while (listOfLogs.size() > MAX_LOGS) {
            listOfLogs.poll();
        }
    }

    public long getCount() {
        return requestCount.get();
    }

    public int getLogCount() {
        return listOfLogs.size();
    }

    public void setCount(int count) {
        requestCount.set(count);
    }
}
