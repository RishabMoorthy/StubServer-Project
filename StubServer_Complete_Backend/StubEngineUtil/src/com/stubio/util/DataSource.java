package com.stubio.util;

import java.util.LinkedList;

public class DataSource {

    String connectionName, driver, host, port, sid, user, pwd;
    LinkedList<GenericProperty> genProp;

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
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

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public LinkedList<GenericProperty> getGenProp() {
        return genProp;
    }

    public void setGenProp(LinkedList<GenericProperty> genProp) {
        this.genProp = genProp;
    }
}
