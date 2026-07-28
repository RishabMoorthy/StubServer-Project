package com.stubio.testUtil;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SecuritySettings {

    @XmlElement(name = "Keystore", namespace = TstNamespace.URI)
    private Store keystore;

    @XmlElement(name = "TrustStore", namespace = TstNamespace.URI)
    private Store trustStore;

    @XmlElement(name = "ClientAuth", namespace = TstNamespace.URI)
    private boolean clientAuth;

    public Store getKeystore() {
        return keystore;
    }

    public void setKeystore(Store keystore) {
        this.keystore = keystore;
    }

    public Store getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(Store trustStore) {
        this.trustStore = trustStore;
    }

    public boolean isClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(boolean clientAuth) {
        this.clientAuth = clientAuth;
    }
}
