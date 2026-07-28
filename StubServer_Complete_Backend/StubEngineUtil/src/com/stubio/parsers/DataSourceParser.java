package com.stubio.parsers;

import com.stubio.util.DataSource;
import com.stubio.util.GenericProperty;
import org.w3c.dom.Element;

import java.util.*;

public final class DataSourceParser {

    private DataSourceParser() {
    }

    public static LinkedHashMap<String, DataSource> parse(Element node) {

        LinkedHashMap<String, DataSource> dataSources =
                new LinkedHashMap<>();

        for (Element dsNode : XmlUtils.childElements(node)) {

            if (!"DataSource".equals(XmlUtils.local(dsNode))) {
                continue;
            }

            DataSource dataSource = new DataSource();
            LinkedList<GenericProperty> properties = new LinkedList<>();

            String connectionName = "";

            for (Element child : XmlUtils.childElements(dsNode)) {

                switch (XmlUtils.local(child)) {

                    case "ConnectionName" -> {
                        connectionName = XmlUtils.text(child);
                        dataSource.setConnectionName(connectionName);
                    }

                    case "Driver" ->
                            dataSource.setDriver(XmlUtils.text(child));

                    case "Host" ->
                            dataSource.setHost(XmlUtils.text(child));

                    case "Port" ->
                            dataSource.setPort(XmlUtils.text(child));

                    case "SID" ->
                            dataSource.setSid(XmlUtils.text(child));

                    case "User" ->
                            dataSource.setUser(XmlUtils.text(child));

                    case "Password" ->
                            dataSource.setPwd(XmlUtils.text(child));

                    case "ConProperty" -> {

                        GenericProperty prop =
                                parseProperty(child);

                        properties.add(prop);
                    }
                }
            }

            if (!properties.isEmpty()) {
                dataSource.setGenProp(properties);
            }

            dataSources.put(connectionName, dataSource);
        }

        return dataSources;
    }

    private static GenericProperty parseProperty(
            Element node) {

        GenericProperty property =
                new GenericProperty();

        for (Element child :
                XmlUtils.childElements(node)) {

            switch (XmlUtils.local(child)) {

                case "Key" ->
                        property.setKey(
                                XmlUtils.text(child));

                case "Value" ->
                        property.setValue(
                                XmlUtils.text(child));
            }
        }

        return property;
    }
}
