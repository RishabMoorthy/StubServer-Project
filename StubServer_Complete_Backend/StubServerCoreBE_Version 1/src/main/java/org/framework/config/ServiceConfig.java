package org.framework.config;

import org.framework.core.BaseRoute;
import org.framework.core.EventHandler;
import org.framework.core.RequestHandler;
import org.framework.core.RouteMatcher;
import org.framework.datasource.DataSourceService;
import org.framework.properties.Properties;

import java.util.List;
import java.util.regex.Matcher;

public class ServiceConfig {
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    private String keystorePath;
    private String keystorePassword;
    private String serviceName;
    private String userName;
    private Properties properties;
    private String wsdlContent;
    private String onRequestScript;
    private String startScript;
    private String stopScript;
    private String afterRequestScript;

    private int totalTxn;
    private int delayPercent;

    private RequestHandler requestHandler;
    private EventHandler eventHandler;

    private int delay = 0;

    private DataSourceService dataSourceService;

    public int getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    private int delayMs = 0;
    private String delayMode = "FIXED";
    private int lowerMs = 0;
    private int upperMs = 0;
    private double medianMs = 0.0;

    public boolean isRouteModeEnabled() {
        return routeModeEnabled;
    }

    public void setRouteModeEnabled(boolean routeModeEnabled) {
        this.routeModeEnabled = routeModeEnabled;
    }

    public String getRouteEndpoint() {
        return routeEndpoint;
    }

    public void setRouteEndpoint(String routeEndpoint) {
        this.routeEndpoint = routeEndpoint;
    }

    private boolean routeModeEnabled = false;
    private String routeEndpoint;

    public DataSourceService getDataSourceService() {
        return dataSourceService;
    }

    public void setDataSourceService(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    private double standardDeviation = 0.0;
    private int port;
    private String autoStart = "false";
    String httpSecure;
    private String protocol;
    private List<BaseRoute> routes; // For REST, TCP, SOAP
    private String type;
    private boolean datasourceEnabled;

    public boolean isDatasourceEnabled() {
        return this.datasourceEnabled;
    }

    public void setDatasourceEnabled(boolean datasourceEnabled) {
        this.datasourceEnabled = datasourceEnabled;
    }

    public String getHttpSecure() {
        return httpSecure;
    }

    public void setHttpSecure(String httpSecure) {
        this.httpSecure = httpSecure;
    }

    public String getWsdlContent() {
        return wsdlContent;
    }

    public void setWsdlContent(String wsdlContent) {
        this.wsdlContent = wsdlContent;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getXmlFileContent() {
        return xmlFileContent;
    }

    public void setXmlFileContent(String xmlFileContent) {
        this.xmlFileContent = xmlFileContent;
    }

    private String xmlFileContent;

    public String getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(String autoStart) {
        this.autoStart = autoStart;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOnRequestScript() {
        return onRequestScript;
    }

    public void setOnRequestScript(String onRequestScript) {
        this.onRequestScript = onRequestScript;
    }

    public String getStartScript() {
        return startScript;
    }

    public void setStartScript(String startScript) {
        this.startScript = startScript;
    }

    public String getStopScript() {
        return stopScript;
    }

    public void setStopScript(String stopScript) {
        this.stopScript = stopScript;
    }

    public String getAfterRequestScript() {
        return afterRequestScript;
    }

    public void setAfterRequestScript(String afterRequestScript) {
        this.afterRequestScript = afterRequestScript;
    }

    public String getDelayMode() {
        return delayMode;
    }

    public void setDelayMode(String delayMode) {
        this.delayMode = delayMode;
    }

    public int getLowerMs() {
        return lowerMs;
    }

    public void setLowerMs(int lowerMs) {
        this.lowerMs = lowerMs;
    }

    public int getUpperMs() {
        return upperMs;
    }

    public void setUpperMs(int upperMs) {
        this.upperMs = upperMs;
    }

    public double getMedianMs() {
        return medianMs;
    }

    public void setMedianMs(double medianMs) {
        this.medianMs = medianMs;
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

    public List<BaseRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<BaseRoute> routes) {
        this.routes = routes;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler handler) {
        this.eventHandler = handler;
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public int getTotalTxn() {
        return totalTxn;
    }

    public void setTotalTxn(int totalTxn) {
        this.totalTxn = totalTxn;
    }

    public int getDelayPercent() {
        return delayPercent;
    }

    public void setDelayPercent(int delayPercent) {
        this.delayPercent = delayPercent;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keyStorePath) {
        this.keystorePath = keyStorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keyStorePassword) {
        this.keystorePassword = keyStorePassword;
    }
}
