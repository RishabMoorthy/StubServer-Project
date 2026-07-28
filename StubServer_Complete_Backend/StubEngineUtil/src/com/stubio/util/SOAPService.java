package com.stubio.util;

import java.util.LinkedList;

public class SOAPService {

    ExecutionMode exeMode = new ExecutionMode();
    SecuritySettings securitySettings = new SecuritySettings();
    DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse();
    LinkedList<StubOperation> stubOperations = new LinkedList<>();

    public SecuritySettings getSecuritySettings() {
        return securitySettings;
    }

    public void setSecuritySettings(SecuritySettings securitySettings) {
        this.securitySettings = securitySettings;
    }

    public ExecutionMode getExeMode() {
        return exeMode;
    }

    public void setExeMode(ExecutionMode exeMode) {
        this.exeMode = exeMode;
    }

    public DefaultErrorResponse getDefaultErrorResponse() {
        return defaultErrorResponse;
    }

    public void setDefaultErrorResponse(DefaultErrorResponse defaultErrorResponse) {
        this.defaultErrorResponse = defaultErrorResponse;
    }

    public LinkedList<StubOperation> getStubOperations() {
        return stubOperations;
    }

    public void setStubOperations(LinkedList<StubOperation> stubOperations) {
        this.stubOperations = stubOperations;
    }
}
