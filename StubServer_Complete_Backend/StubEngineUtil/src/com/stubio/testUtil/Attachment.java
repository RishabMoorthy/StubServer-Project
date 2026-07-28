package com.stubio.testUtil;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Attachment {

    @XmlElement(name = "Name", namespace = TstNamespace.URI)
    private String name;

    @XmlElement(name = "ContentType", namespace = TstNamespace.URI)
    private String contentType;

    @XmlElement(name = "Size", namespace = TstNamespace.URI)
    private long size;

    @XmlElement(name = "ContentId", namespace = TstNamespace.URI)
    private String contentId;

    @XmlElement(name = "Url", namespace = TstNamespace.URI)
    private String url;

    @XmlElement(name = "Data", namespace = TstNamespace.URI)
    private String data;

    @XmlElement(name = "Id", namespace = TstNamespace.URI)
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
