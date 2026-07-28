package com.stubio.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Endpoint {

    String name, path, method, defaultRR, id;
    private DataSourceSelect dataSourceSelect;
    private LinkedList<DataGenerator> dataGenerators;
    LinkedList<RRPair> rrList = new LinkedList<>();
    LinkedList<Filter> filterList = new LinkedList<>();
    LinkedHashMap<String, Request> requestList = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDefaultRR() {
        return defaultRR;
    }

    public void setDefaultRR(String defaultRR) {
        this.defaultRR = defaultRR;
    }

    public LinkedList<DataGenerator> getDataGenerators() {
        return dataGenerators;
    }

    public void setDataGenerators(LinkedList<DataGenerator> dataGenerators) {
        this.dataGenerators = dataGenerators;
    }

    public LinkedList<RRPair> getRrList() {
        return rrList;
    }

    public void setRrList(LinkedList<RRPair> rrList) {
        this.rrList = rrList;
    }

    public DataSourceSelect getDataSourceSelect() {
        return dataSourceSelect;
    }

    public void setDataSourceSelect(DataSourceSelect dataSourceSelect) {
        this.dataSourceSelect = dataSourceSelect;
    }

    public LinkedList<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(LinkedList<Filter> filterList) {
        this.filterList = filterList;
    }

    public LinkedHashMap<String, Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(LinkedHashMap<String, Request> requestList) {
        this.requestList = requestList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
