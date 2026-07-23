package org.framework.core;

import java.util.List;

import org.framework.datasource.DataSourceRegistry;
import org.framework.properties.Properties;

public class ParsedXMLObject {

    private List<BaseRoute> routes;
    private int port;
    private String protocol;
    private String type;
    private boolean datasource;
    private String wsdlcontent;
    private Properties properties;
    private String httpSecure;
    private boolean routeModeEnabled;

    public String getRouteEndpoint() {
        return routeEndpoint;
    }

    public void setRouteEndpoint(String routeEndpoint) {
        this.routeEndpoint = routeEndpoint;
    }

    public boolean isRouteModeEnabled() {
        return routeModeEnabled;
    }

    public void setRouteModeEnabled(boolean routeModeEnabled) {
        this.routeModeEnabled = routeModeEnabled;
    }

    private String routeEndpoint;

    private DataSourceRegistry dataSourceRegistry;
    
    public boolean isDatasource() {
        return this.datasource;
    }

    public void setDatasource(boolean datasource) {
        this.datasource = datasource;
    }

    public DataSourceRegistry getdataSourceRegistry() {
        return dataSourceRegistry;
    }

    public void setdataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getWsdlcontent() {
        return wsdlcontent;
    }

    public void setWsdlcontent(String wsdlcontent) {
        this.wsdlcontent = wsdlcontent;
    }

    public String getHttpSecure() {
        return httpSecure;
    }

    public void setHttpSecure(String httpSecure) {
        this.httpSecure = httpSecure;
    }

    public Exception getexception() {
        return exception;
    }

    public void setexception(Exception e) {
        this.exception = e;
    }

    private Exception exception;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    private int delay;

    public String getXmlFileContent() {
        return xmlFileContent;
    }

    public void setXmlFileContent(String xmlFileContent) {
        this.xmlFileContent = xmlFileContent;
    }

    private String xmlFileContent;

    String autoStart;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(String autoStart) {
        this.autoStart = autoStart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getOnRequestScript() {
        return onRequestScript;
    }

    public void setOnRequestScript(String onRequestScript) {
        this.onRequestScript = onRequestScript;
    }

    public String getAfterRequestScript() {
        return afterRequestScript;
    }

    public void setAfterRequestScript(String afterRequestScript) {
        this.afterRequestScript = afterRequestScript;
    }

    public String getStopScript() {
        return stopScript;
    }

    public void setStopScript(String stopScript) {
        this.stopScript = stopScript;
    }

    public String getStartScript() {
        return startScript;
    }

    public void setStartScript(String startScript) {
        this.startScript = startScript;
    }

    String onRequestScript;
    String startScript;
    String stopScript;
    String afterRequestScript;
    public List<BaseRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<BaseRoute> routes) {
        this.routes = routes;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
