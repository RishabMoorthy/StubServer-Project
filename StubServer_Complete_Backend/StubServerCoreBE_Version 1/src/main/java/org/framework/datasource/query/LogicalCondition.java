package org.framework.datasource.query;

import java.util.List;

public class LogicalCondition implements Condition{
    private List<Condition> condition;
    private final ConditionType conditionType;

    public LogicalCondition(ConditionType conditionType, List<Condition> list) {
        this.conditionType = conditionType;
        this.condition = list;
    }

    public List<Condition> getCondition() {
        return condition;
    }

    public void setCondition(List<Condition> condition) {
        this.condition = condition;
    }

    @Override
    public ConditionType getType() {
        return conditionType;
    }
}
