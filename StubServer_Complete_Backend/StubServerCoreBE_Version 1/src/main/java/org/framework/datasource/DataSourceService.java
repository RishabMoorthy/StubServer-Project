package org.framework.datasource;

public class DataSourceService {

    private final DataSourceFactory factory;

    private final DataSourceRegistry registry;

    public DataSourceRegistry getRegistry() {
        return registry;
    }

    public DataSourceFactory getFactory() {
        return factory;
    }

    public DataSourceService(DataSourceRegistry registry, DataSourceFactory factory) {
        this.registry = registry;
        this.factory = factory;
    }
}
