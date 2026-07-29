package org.framework.datasource.query;

public class RequestColumnMapping {
    private String columnName;
    private ComparisonOperator comparison;
    private String requestParameter;

    public RequestColumnMapping(String columnName, ComparisonOperator comparison, String requestParameter) {
        this.columnName = columnName;
        this.comparison = comparison;
        this.requestParameter = requestParameter;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ComparisonOperator getComparison() {
        return comparison;
    }

    public void setComparison(ComparisonOperator comparison) {
        this.comparison = comparison;
    }

    public String getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(String requestParameter) {
        this.requestParameter = requestParameter;
    }
}
