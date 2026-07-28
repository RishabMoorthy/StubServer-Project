package com.stubio.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class StubOperation {

    String name, bindingstubOperationName, defaultRR;
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

    public String getBindingstubOperationName() {
        return bindingstubOperationName;
    }

    public void setBindingstubOperationName(String bindingstubOperationName) {
        this.bindingstubOperationName = bindingstubOperationName;
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
}
