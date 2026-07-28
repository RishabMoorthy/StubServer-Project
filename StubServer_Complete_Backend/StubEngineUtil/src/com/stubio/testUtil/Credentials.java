package com.stubio.testUtil;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Credentials {

    @XmlElement(name = "SelectedAuthProfile", namespace = TstNamespace.URI)
    private String selectedAuthProfile;

    @XmlElement(name = "AuthType", namespace = TstNamespace.URI)
    private String authType;

    public String getSelectedAuthProfile() {
        return selectedAuthProfile;
    }

    public void setSelectedAuthProfile(String selectedAuthProfile) {
        this.selectedAuthProfile = selectedAuthProfile;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }
}
