package org.framework.datasource.query;

public class LeafCondition implements Condition{
    private RequestColumnMapping mapping;

    public LeafCondition(RequestColumnMapping mapping){
        this.mapping = mapping;
    }

    public RequestColumnMapping getMapping() {
        return mapping;
    }

    public void setMapping(RequestColumnMapping mapping) {
        this.mapping = mapping;
    }

    public ConditionType getType() {
        return ConditionType.LEAF;
    }
}
