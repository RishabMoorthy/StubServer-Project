package com.stubio.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class VirtualServiceObject {

    int port;
    String vsName = "";
    String host, xmlPath, path, vsType, id, contextPath, environment, createdVersion, lastUpdated, encryption;
    int maxThreads, coreThreads, responseDelay;
    boolean allowFallback = false;
    boolean autoStart = false;
    boolean secured;
    WSDLMetadata wsdlMetaData = new WSDLMetadata();

    LinkedList<GenericProperty> propertyList = new LinkedList<GenericProperty>();
    LinkedHashMap<String, DataSource> dataSourceList = new LinkedHashMap<>();
    LinkedHashMap<String, Script> customScripts = new LinkedHashMap<>();
    ExecutionMode exeMode = new ExecutionMode();
    DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse();

    RestService restService = new RestService();
    SOAPService soapService = new SOAPService();

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVsType() {
        return vsType;
    }

    public void setVsType(String vsType) {
        this.vsType = vsType;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVsName() {
        return vsName;
    }

    public void setVsName(String vsName) {
        this.vsName = vsName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public LinkedHashMap<String, Script> getCustomScripts() {
        return customScripts;
    }

    public void setCustomScripts(LinkedHashMap<String, Script> customScripts) {
        this.customScripts = customScripts;
    }

    public LinkedHashMap<String, DataSource> getDataSourceList() {
        return dataSourceList;
    }

    public void setDataSourceList(LinkedHashMap<String, DataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
    }

    public ExecutionMode getExeMode() {
        return exeMode;
    }

    public void setExeMode(ExecutionMode exeMode) {
        this.exeMode = exeMode;
    }

    public WSDLMetadata getWsdlMetaData() {
        return wsdlMetaData;
    }

    public void setWsdlMetaData(WSDLMetadata wsdlMetaData) {
        this.wsdlMetaData = wsdlMetaData;
    }

    public LinkedList<GenericProperty> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(LinkedList<GenericProperty> propertyList) {
        this.propertyList = propertyList;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getCoreThreads() {
        return coreThreads;
    }

    public void setCoreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
    }

    public int getResponseDelay() {
        return responseDelay;
    }

    public void setResponseDelay(int responseDelay) {
        this.responseDelay = responseDelay;
    }

    public RestService getRestService() {
        return restService;
    }

    public DefaultErrorResponse getDefaultErrorResponse() {
        return defaultErrorResponse;
    }

    public void setDefaultErrorResponse(DefaultErrorResponse defaultErrorResponse) {
        this.defaultErrorResponse = defaultErrorResponse;
    }

    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    public SOAPService getSoapService() {
        return soapService;
    }

    public void setSoapService(SOAPService soapService) {
        this.soapService = soapService;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getCreatedVersion() {
        return createdVersion;
    }

    public void setCreatedVersion(String createdVersion) {
        this.createdVersion = createdVersion;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public boolean isAllowFallback() {
        return allowFallback;
    }

    public void setAllowFallback(boolean allowFallback) {
        this.allowFallback = allowFallback;
    }
}
