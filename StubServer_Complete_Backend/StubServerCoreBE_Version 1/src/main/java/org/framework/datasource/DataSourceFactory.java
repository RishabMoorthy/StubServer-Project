package org.framework.datasource;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceFactory {

    private final Map<String, DataSource> sequentialCache =
            new ConcurrentHashMap<>();

    public DataSource create(DataSourceDefinition definition) {
        if (definition == null) {
            throw new DataSourceException("DataSourceDefinition cannot be null.");
        }

        if (definition.getAccessMode() == AccessMode.QUERY) {
            return createFresh(definition);
        }

        return sequentialCache.computeIfAbsent(
                definition.getName(),
                name -> createFresh(definition)
        );
    }

    private DataSource createFresh(DataSourceDefinition definition) {
        DataSource dataSource = switch (definition.getType()) {
            case EXCEL -> createExcelDataSource(definition);
            case CSV -> throw new UnsupportedOperationException("CSV datasource not implemented yet.");
            case SQL -> throw new UnsupportedOperationException("SQL datasource not implemented yet.");
        };

        applyProperties(dataSource, definition.getProperties());

        dataSource.setPropertyValue(
                ExcelDataSource.PROP_ACCESS_MODE,
                definition.getAccessMode().name()
        );

        dataSource.resetAndLoad();
        return dataSource;
    }

    private DataSource createExcelDataSource(DataSourceDefinition definition) {
        Object filePathValue = definition.getProperties().get("filePath");
        if (filePathValue == null) {
            throw new DataSourceException("Missing required property 'filePath' for Excel datasource: " + definition.getName());
        }

        return new ExcelDataSource(Path.of(String.valueOf(filePathValue)));
    }

    private void applyProperties(DataSource dataSource, Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if ("filePath".equals(entry.getKey())) {
                // filePath already used in constructor
                continue;
            }
            dataSource.setPropertyValue(entry.getKey(), entry.getValue());
        }
    }
}
