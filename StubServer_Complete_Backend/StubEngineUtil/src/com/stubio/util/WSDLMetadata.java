package com.stubio.util;

public class WSDLMetadata {

    private String type;        // TEXT / URL etc
    private String rootPart;    // file path

    private String url;         // <WSDLURL>
    private String wsdlText;    // <WSDLText>

    public WSDLMetadata() {
    }

    public WSDLMetadata(String type, String rootPart, String url, String wsdlText) {
        this.type = type;
        this.rootPart = rootPart;
        this.url = url;
        this.wsdlText = wsdlText;
    }

    // Getters & Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRootPart() {
        return rootPart;
    }

    public void setRootPart(String rootPart) {
        this.rootPart = rootPart;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWsdlText() {
        return wsdlText;
    }

    public void setWsdlText(String wsdlText) {
        this.wsdlText = wsdlText;
    }

    @Override
    public String toString() {
        return "WSDLMetadata{" +
                "type='" + type + '\'' +
                ", rootPart='" + rootPart + '\'' +
                ", url='" + url + '\'' +
                ", wsdlText='" + (wsdlText != null ? "[CONTENT]" : null) + '\'' +
                '}';
    }
}
