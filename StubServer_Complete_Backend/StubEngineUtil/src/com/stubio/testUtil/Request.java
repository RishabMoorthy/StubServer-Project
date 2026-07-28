package com.stubio.testUtil;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Request {

    @XmlAttribute
    private String mediaType;

    @XmlAttribute
    private boolean postQueryString;

    @XmlValue
    private String body; // JSON payload

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isPostQueryString() {
        return postQueryString;
    }

    public void setPostQueryString(boolean postQueryString) {
        this.postQueryString = postQueryString;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
