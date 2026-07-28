package com.stubio.util;

import java.util.LinkedList;

public class RestService {


    SecuritySettings securitySettings = new SecuritySettings();

    LinkedList<Endpoint> endpoints = new LinkedList<>();

    public SecuritySettings getSecuritySettings() {
        return securitySettings;
    }

    public void setSecuritySettings(SecuritySettings securitySettings) {
        this.securitySettings = securitySettings;
    }



    public LinkedList<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(LinkedList<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
