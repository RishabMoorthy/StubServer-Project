package com.stubio.util;

import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private String connectionName, dsName;
    private String query;
    private List<GenericProperty> resultProperties = new ArrayList<>();

    public String getConnectionName() {
        return connectionName;
    }
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }

    public List<GenericProperty> getResultProperties() {
        return resultProperties;
    }
    public void setResultProperties(List<GenericProperty> resultProperties) {
        this.resultProperties = resultProperties;
    }

    public String getDsName() {
        return dsName;
    }
    public void setDsName(String dsName) {
        this.dsName = dsName;
    }
}
