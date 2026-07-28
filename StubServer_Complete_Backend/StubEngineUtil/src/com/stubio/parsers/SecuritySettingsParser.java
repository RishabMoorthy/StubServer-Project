package com.stubio.parsers;

import com.stubio.util.*;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;

public final class SecuritySettingsParser {

    private SecuritySettingsParser() {
    }

    public static SecuritySettings parse(
            Element securityNode) {

        SecuritySettings settings =
                new SecuritySettings();

        LinkedHashMap<String, KeyStore> keyStoreMap =
                new LinkedHashMap<>();

        for (Element child :
                XmlUtils.childElements(securityNode)) {

            switch (XmlUtils.local(child)) {

                case "Keystore" ->
                        keyStoreMap.put(
                                "KeyStore",
                                parseKeyStore(child));

                case "TrustStore" ->
                        keyStoreMap.put(
                                "TrustStore",
                                parseKeyStore(child));

                case "ClientAuth" ->
                        settings.setClientAuth(
                                Boolean.parseBoolean(
                                        XmlUtils.text(child)));
            }
        }

        settings.setKeyStoreMap(keyStoreMap);

        return settings;
    }

    private static KeyStore parseKeyStore(
            Element storeNode) {

        KeyStore keyStore =
                new KeyStore();

        for (Element child :
                XmlUtils.childElements(storeNode)) {

            switch (XmlUtils.local(child)) {

                case "Path" ->
                        keyStore.setPath(
                                XmlUtils.text(child));

                case "Password" ->
                        keyStore.setKeyPass(
                                XmlUtils.text(child));

                case "Type" ->
                        keyStore.setType(
                                XmlUtils.text(child));

                case "KeyAlias" ->
                        keyStore.setKeyAlias(
                                XmlUtils.text(child));
            }
        }

        return keyStore;
    }
}
