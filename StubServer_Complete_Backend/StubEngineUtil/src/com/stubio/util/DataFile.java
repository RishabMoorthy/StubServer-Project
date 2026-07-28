package com.stubio.util;

import java.util.LinkedList;

public class DataFile {

    private String connectionName, dsName, requestName;
    private String fileLocation;
    private String fileType, mappingType;
    private String sheet;
    private Request request;
    // RequestColumnMappings contains multiple RequestColumnMapping plus a between-condition (AND/OR)
    private LinkedList<RequestColumnMapping> mappings = new LinkedList<>();

    public String getConnectionName() {
        return connectionName;
    }
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getFileLocation() {
        return fileLocation;
    }
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getSheet() {
        return sheet;
    }
    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public LinkedList<RequestColumnMapping> getMappings() {
        return mappings;
    }
    public void setMappings(LinkedList<RequestColumnMapping> mappings) {
        this.mappings = mappings;
    }

    public String getMappingType() {
        return mappingType;
    }

    public void setMappingType(String mappingType) {
        this.mappingType = mappingType;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }
}
