package org.framework.datasource;

public interface SequentialDataSource {

    /**
     * Returns the next value for the given column
     * in round-robin order.
     */
    String getNextValue(String columnName);
}
