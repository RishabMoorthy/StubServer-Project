package com.stubio.util;

import java.util.LinkedHashMap;

public class SecuritySettings {

    LinkedHashMap<String, KeyStore> keyStoreMap = new LinkedHashMap<>();
    KeyStore keyStore = new KeyStore();

    boolean clientAuth = false;

    public LinkedHashMap<String, KeyStore> getKeyStoreMap() {
        return keyStoreMap;
    }

    public void setKeyStoreMap(LinkedHashMap<String, KeyStore> keyStoreMap) {
        this.keyStoreMap = keyStoreMap;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public boolean isClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(boolean clientAuth) {
        this.clientAuth = clientAuth;
    }
}
