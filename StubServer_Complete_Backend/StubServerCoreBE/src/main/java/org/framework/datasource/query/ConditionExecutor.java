package org.framework.datasource.query;

import groovy.util.logging.Log;
import org.framework.datasource.DataSource;
import org.framework.properties.MockRequest;
import org.framework.utils.RequestParser;

import java.util.HashMap;
import java.util.Map;

public class ConditionExecutor {
    DataSource dataSource;
    MockRequest request;
    RequestParser parser = new RequestParser();

    public ConditionExecutor(DataSource dataSource, MockRequest request){
        this.dataSource = dataSource;
        this.request =request;
    }

    public Map<String, String> parseCondition(Condition condition) {
        dataSource.resetAndLoad();
        while (dataSource.next()) {
            switch (condition.getType()){
                case LEAF ->
                {
                    LeafCondition c = (LeafCondition) condition;
                    if (parseCondition(c, dataSource.getDataPropertyValue(c.getMapping().getColumnName()))){
                        return dataSource.getCurrentRow();
                    }
                }

                case OR, AND ->{
                    LogicalCondition c =  (LogicalCondition) condition;
                    if (parseCondition(c)){
                        return dataSource.getCurrentRow();
                    }
                }
            }
        }
        dataSource.close();
        return null;
    }

    boolean parseCondition(LogicalCondition condition){
        for (Condition c : condition.getCondition()){
            if (c.getType() == ConditionType.LEAF){
                LeafCondition lc = (LeafCondition) c;
                boolean result = parseCondition(lc, dataSource.getDataPropertyValue(lc.getMapping().getColumnName()));
                if (result && condition.getType() == ConditionType.OR){
                    return true;
                } else if (!result && condition.getType() == ConditionType.AND){
                    return false ;
                }
            } else {
                LogicalCondition lc = (LogicalCondition) c;
                boolean result = parseCondition(lc);
                if (result && condition.getType() ==  ConditionType.OR){
                    return true;
                }else if(!result && condition.getType() == ConditionType.AND){
                    return false;
                }
            }
        }

        return condition.getType() == ConditionType.AND;
    }
    
    boolean parseCondition(LeafCondition condition, String columnValue) {
        String value = parser.getRequestValue(request.getRequestContent(), condition.getMapping().getRequestParameter()).toString();

        switch (condition.getMapping().getComparison()){
            case ComparisonOperator.ENDS_WITH -> {
                return value.endsWith(columnValue);
            }
            case ComparisonOperator.STARTS_WITH -> {
                return value.startsWith(columnValue);
            }
            case ComparisonOperator.EQUALS ->  {
                return value.equalsIgnoreCase(columnValue);
            }
            case ComparisonOperator.CONTAINS -> {
                return value.contains(columnValue);
            }
        }

        return false;
    }
}
