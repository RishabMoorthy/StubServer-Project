package org.framework.config;

import org.framework.constants.PathConstants;
import org.framework.datasource.AccessMode;
import org.framework.datasource.DataSourceDefinition;
import org.framework.datasource.DataSourceRegistry;
import org.framework.datasource.DataSourceType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Parser {

    private final XPath xpath = XPathFactory.newInstance().newXPath();

    public DataSourceRegistry parseDataSources(Document document, String serviceName) throws Exception {
        DataSourceRegistry registry = new DataSourceRegistry();

        // Find ALL File data sources (0..N)
        XPathExpression expr = xpath.compile(
                "//*[local-name()='DataSourceSelect']/*[local-name()='File']");

        NodeList fileNodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        if (fileNodes.getLength() == 0) {
            System.out.println("No File DataSources found.");
            return registry;
        }

        for (int i = 0; i < fileNodes.getLength(); i++) {
            Node fileNode = fileNodes.item(i);
            parseFileDataSource(fileNode, registry, serviceName);
        }

        return registry;
    }

    private void parseFileDataSource(Node fileNode,
            DataSourceRegistry registry,
            String serviceName) throws Exception {

        String connectionName = getChildText(fileNode, "ConnectionName");
        String fileTypeValue = getChildText(fileNode, "FileType");
        DataSourceType dataSourceType = mapFileType(fileTypeValue);

        if (dataSourceType == null) {
            System.out.println("Unsupported FileType: " + fileTypeValue);
            return;
        }

        String xmlFilePath = getChildText(fileNode, "FileLocation");

        Path path = Paths.get(xmlFilePath);
        String fileName = path.getFileName().toString();

        String sheet = getChildText(fileNode, "Sheet");
        String fileLocation = PathConstants.DATASET_BASE_PATH + serviceName + File.separator + fileName;
        AccessMode accessMode = resolveAccessMode(fileNode);

        DataSourceDefinition definition = new DataSourceDefinition(connectionName, dataSourceType, accessMode)
                .addProperty("filePath", fileLocation)
                .addProperty("worksheet", sheet)
                .addProperty("headerRowIndex", 0);

        if (accessMode == AccessMode.RANDOM_WINDOW) {
            String windowSizeValue = getChildText(fileNode, "RandomWindowSize");

            if (windowSizeValue == null || windowSizeValue.isBlank()) {
                throw new IllegalArgumentException(
                        "RandomWindowSize is required for FetchType=RandomWindow");
            }

            definition.addProperty("windowSize", Integer.parseInt(windowSizeValue.trim()));
        }

        registry.register(definition);

        System.out.println("Registered File DataSource: " + connectionName +
                " with type " + dataSourceType);
    }

    /**
     * Maps XML FileType → Enum
     */
    private DataSourceType mapFileType(String fileType) {
        if (fileType == null)
            return null;

        switch (fileType.trim().toUpperCase()) {
            case "EXCEL":
                return DataSourceType.EXCEL;
            case "CSV":
                return DataSourceType.CSV;
            case "XML":
                // map XML files if needed; else return null
                return DataSourceType.CSV; // OR create XML enum later
            default:
                return null;
        }
    }

    private AccessMode resolveAccessMode(Node fileNode) throws XPathExpressionException {

        String fetchType = getChildText(fileNode, "FetchType");

        if (fetchType == null || fetchType.isBlank()) {
            return AccessMode.QUERY;
        }

        switch (fetchType.trim().toUpperCase()) {
            case "SEQUENTIAL":
                return AccessMode.SEQUENTIAL;
            case "RANDOM":
                return AccessMode.RANDOM;
            case "RANDOMWINDOW":
                return AccessMode.RANDOM_WINDOW;
            default:
                throw new IllegalArgumentException(
                        "Unsupported FetchType: " + fetchType);
        }
    }

    private String getChildText(Node parent, String childName) throws XPathExpressionException {
        XPathExpression expr = xpath.compile("*[local-name()='" + childName + "']/text()");
        return (String) expr.evaluate(parent, XPathConstants.STRING);
    }
}
