package org.framework.datasource;

import java.util.HashMap;
import java.util.Map;

public class DataSourceRegistry {

    private final Map<String, DataSourceDefinition> definitions = new HashMap<>();

    public void register(DataSourceDefinition definition) {
        if (definition == null) {
            throw new DataSourceException("DataSourceDefinition cannot be null.");
        }

        if (definition.getName() == null || definition.getName().trim().isEmpty()) {
            throw new DataSourceException("Datasource name cannot be null or empty.");
        }

        definitions.put(definition.getName(), definition);
    }

    public DataSourceDefinition getDefinition(String name) {
        DataSourceDefinition definition = definitions.get(name);
        if (definition == null) {
            throw new DataSourceException("No datasource registered with name: " + name);
        }
        return definition;
    }
}
