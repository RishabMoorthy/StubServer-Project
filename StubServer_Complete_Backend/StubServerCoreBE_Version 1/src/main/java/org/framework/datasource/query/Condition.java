package org.framework.datasource.query;
/* A & B & (C OR D)

<Condition type="AND">

    <Condition type="LEAF">
        <ColumnName>Account</ColumnName>
        <Operator>EQUALS</Operator>
        <Values>
            <Value>Sample.Request.field1</Value>
        </Values>
    </Condition>

  <Condition type="LEAF">
        <ColumnName>Account</ColumnName>
        <Operator>EQUALS</Operator>
        <Values>
            <Value>Sample.Request.field1</Value>
        </Values>
    </Condition>

    <Condition type="OR">

        <Condition type="LEAF">
            <ColumnName>Name</ColumnName>
            <Operator>STARTS_WITH</Operator>
            <Values>
                <Value>Sample.Request.field2</Value>
            </Values>
        </Condition>

        <Condition type="LEAF">
            <ColumnName>Email</ColumnName>
            <Operator>ENDS_WITH</Operator>
            <Values>
                <Value>@example.com</Value>
            </Values>
        </Condition>

    </Condition>

</Condition>

* */

public interface Condition {
    ConditionType getType();
}
