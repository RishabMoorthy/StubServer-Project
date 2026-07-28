package com.stubio.parsers;

import com.stubio.util.*;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class DataSourceSelectParser {

    private DataSourceSelectParser() {
    }

    public static DataSourceSelect parse(
            Element node,
            Object owner,
            VirtualServiceObject vso) {

        DataSourceSelect select = new DataSourceSelect();

        LinkedList<DataBase> databases = new LinkedList<>();

        LinkedList<DataFile> files = new LinkedList<>();

        for (Element child : XmlUtils.childElements(node)) {

            switch (XmlUtils.local(child)) {

                case "Database" ->
                        databases.add(
                                parseDatabase(child));

                case "File" ->
                        files.add(
                                parseFile(
                                        child,
                                        owner));
            }
        }

        select.setDatabase(databases);
        select.setFile(files);

        return select;
    }

    private static DataBase parseDatabase(
            Element dbNode) {

        DataBase database = new DataBase();

        List<GenericProperty> resultProperties = new ArrayList<>();

        for (Element child : XmlUtils.childElements(dbNode)) {

            switch (XmlUtils.local(child)) {

                case "ConnectionName" ->
                        database.setConnectionName(
                                XmlUtils.text(child));

                case "DataSourceName" ->
                        database.setDsName(
                                XmlUtils.text(child));

                case "Query" ->
                        database.setQuery(
                                child.getTextContent());

                case "ResultProperties" -> {

                    for (Element rp : XmlUtils.childElements(child)) {

                        GenericProperty property = new GenericProperty();

                        for (Element field : XmlUtils.childElements(rp)) {

                            switch (XmlUtils.local(field)) {

                                case "Name" ->
                                        property.setKey(
                                                XmlUtils.text(field));

                                case "ColumnName" ->
                                        property.setValue(
                                                XmlUtils.text(field));
                            }
                        }

                        resultProperties.add(property);
                    }
                }
            }
        }

        database.setResultProperties(
                resultProperties);

        return database;
    }

    private static DataFile parseFile(
            Element fileNode,
            Object owner) {

        DataFile file = new DataFile();

        LinkedList<RequestColumnMapping> mappings = new LinkedList<>();

        for (Element child : XmlUtils.childElements(fileNode)) {

            switch (XmlUtils.local(child)) {

                case "ConnectionName" ->
                        file.setConnectionName(
                                XmlUtils.text(child));

                case "DataSourceName" ->
                        file.setDsName(
                                XmlUtils.text(child));

                case "FileLocation" ->
                        file.setFileLocation(
                                XmlUtils.text(child));

                case "FileType" ->
                        file.setFileType(
                                XmlUtils.text(child));

                case "Sheet" ->
                        file.setSheet(
                                XmlUtils.text(child));

                case "MappingType" ->
                        file.setMappingType(
                                XmlUtils.text(child));

                case "RequestName" -> {

                    String requestName = XmlUtils.text(child);

                    file.setRequestName(
                            requestName);

                    attachRequest(
                            file,
                            owner,
                            requestName);
                }

                case "RequestColumnMappings" ->
                        mappings.addAll(
                                parseRequestColumnMappings(
                                        child));
            }
        }

        file.setMappings(mappings);

        return file;
    }

    private static void attachRequest(
            DataFile file,
            Object owner,
            String requestName) {

        if (owner instanceof Endpoint endpoint
                && endpoint.getRequestList() != null) {

            file.setRequest(
                    endpoint.getRequestList()
                            .get(requestName));
        }

        if (owner instanceof StubOperation operation
                && operation.getRequestList() != null) {

            file.setRequest(
                    operation.getRequestList()
                            .get(requestName));
        }
    }

    private static List<RequestColumnMapping> parseRequestColumnMappings(
            Element mappingsNode) {

        List<RequestColumnMapping> mappings = new ArrayList<>();

        for (Element mappingNode : XmlUtils.childElements(mappingsNode)) {

            if (!"RequestColumnMapping".equals(
                    XmlUtils.local(mappingNode))) {
                continue;
            }

            RequestColumnMapping mapping = new RequestColumnMapping();

            for (Element child : XmlUtils.childElements(mappingNode)) {

                switch (XmlUtils.local(child)) {

                    case "RequestMapping" ->
                            mapping.setRequestParameter(
                                    XmlUtils.text(child));

                    case "ComparisonType" ->
                            mapping.setComparisonType(
                                    XmlUtils.text(child));

                    case "ColumnName" ->
                            mapping.setColumnName(
                                    XmlUtils.text(child));
                }
            }

            mappings.add(mapping);
        }

        return mappings;
    }
}
