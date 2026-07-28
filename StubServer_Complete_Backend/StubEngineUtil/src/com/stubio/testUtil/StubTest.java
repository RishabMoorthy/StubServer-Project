package com.stubio.testUtil;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "StubTest", namespace = TstNamespace.URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class StubTest {


    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String method;

    @XmlAttribute
    private String lastUpdated;

    private String path;

    @XmlElement(name = "SecuritySettings", namespace = TstNamespace.URI)
    private SecuritySettings securitySettings;

    @XmlElement(name = "Endpoints", namespace = TstNamespace.URI)
    private Endpoints endpoints;

    @XmlElement(name = "URIs", namespace = TstNamespace.URI)
    private URIs uris;

    @XmlElement(name = "Headers", namespace = TstNamespace.URI)
    private Headers headers;

    @XmlElement(name = "Credentials", namespace = TstNamespace.URI)
    private Credentials credentials;

    @XmlElement(name = "Attachments", namespace = TstNamespace.URI)
    private Attachments attachments;

    @XmlElement(name = "Request", namespace = TstNamespace.URI)
    private Request request;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SecuritySettings getSecuritySettings() {
        return securitySettings;
    }

    public void setSecuritySettings(SecuritySettings securitySettings) {
        this.securitySettings = securitySettings;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public URIs getUris() {
        return uris;
    }

    public void setUris(URIs uris) {
        this.uris = uris;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Attachments getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments attachments) {
        this.attachments = attachments;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
