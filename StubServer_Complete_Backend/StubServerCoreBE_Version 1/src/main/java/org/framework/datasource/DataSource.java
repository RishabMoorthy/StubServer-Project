package org.framework.datasource;

import java.util.Map;

public interface DataSource extends AutoCloseable {

    /**
     * Sets a runtime property on the datasource.
     * Example properties:
     * - worksheet
     * - headerRowIndex
     * - dataStartRowIndex
     * - skipEmptyRows
     */
    void setPropertyValue(String key, Object value);

    /**
     * Gets a previously set runtime property.
     */
    Object getPropertyValue(String key);

    /**
     * Loads or reloads the datasource using the current properties.
     * Must be called after changing properties like worksheet.
     */
    void resetAndLoad();

    /**
     * Moves to the next data row.
     *
     * @return true if next row exists, false otherwise
     */
    boolean next();

    /**
     * Returns the value of the given column name from the current row.
     *
     * @param columnName header name from the Excel sheet
     * @return string value of the cell, empty string if blank
     */
    String getDataPropertyValue(String columnName);

    public Map<String, String> getCurrentRow();

    @Override
    void close();
}

    /**
     * Returns the full current row as columnName -> value map.
     */
