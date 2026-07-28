package com.stubio.util;

import java.util.ArrayList;
import java.util.List;

public class ExecutionMode {

    public static class LiveURL {

        public LiveURL() {
        }

        String transportType;
        String host;
        String port;
        String envType;
        String basePath;

        String SSLToServerKeyStoreFile;
        String SSLToServerKeystorePwd;

        String SSLToClientKeyStoreFile;
        String SSLToClientKeyStorePwd;

        boolean sslToClient;

        public String getSSLToClientKeyStoreFile() {
            return SSLToClientKeyStoreFile;
        }

        public String getSSLToClientKeyStorePwd() {
            return SSLToClientKeyStorePwd;
        }

        public boolean isSslToClient() {
            return sslToClient;
        }

        public void setSSLToClientKeyStorePwd(String SSLToClientKeyStorePwd) {
            this.SSLToClientKeyStorePwd = SSLToClientKeyStorePwd;
        }

        public void setSSLToClientKeyStoreFile(String SSLToClientKeyStoreFile) {
            this.SSLToClientKeyStoreFile = SSLToClientKeyStoreFile;
        }

        public void setSslToClient(boolean sslToClient) {
            this.sslToClient = sslToClient;
        }

        public void setSSLToServerKeyStoreFile(String SSLToServerKeyStoreFile) {
            this.SSLToServerKeyStoreFile = SSLToServerKeyStoreFile;
        }

        public void setSSLToServerKeystorePwd(String SSLToServerKeystorePwd) {
            this.SSLToServerKeystorePwd = SSLToServerKeystorePwd;
        }

        public void setSSLToServer(boolean SSLToServer) {
            isSSLToServer = SSLToServer;
        }

        private boolean active;

        public boolean isSSLToServer() {
            return isSSLToServer;
        }

        public String getSSLToServerKeystorePwd() {
            return SSLToServerKeystorePwd;
        }

        public String getSSLToServerKeyStoreFile() {
            return SSLToServerKeyStoreFile;
        }

        private boolean isSSLToServer; // NEW

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getTransportType() {
            return transportType;
        }

        public void setTransportType(String transportType) {
            this.transportType = transportType;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getEnvType() {
            return envType;
        }

        public void setEnvType(String envType) {
            this.envType = envType;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }

    private List<LiveURL> liveURLs = new ArrayList<>();
    String exeModeValue;

    public List<LiveURL> getLiveURLs() {
        return liveURLs;
    }

    public void setLiveURLs(List<LiveURL> liveURLs) {
        this.liveURLs = liveURLs;
    }

    public String getExeModeValue() {
        return exeModeValue;
    }

    public void setExeModeValue(String exeModeValue) {
        this.exeModeValue = exeModeValue;
    }
}
