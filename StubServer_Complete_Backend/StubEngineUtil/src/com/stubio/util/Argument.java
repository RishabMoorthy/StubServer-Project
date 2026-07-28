package com.stubio.util;

public class Argument {


    private String name;        // attribute
    private String matchType;   // attribute, e.g., "=" or ">"
    private String caseSensitive; // from attribute "case"
    private String echoValue;     // attribute "echoValue"
    private String value;       // element text content

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getMatchType() {
        return matchType;
    }
    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String isCaseSensitive() {
        return caseSensitive;
    }
    public void setCaseSensitive(String caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String isEchoValue() {
        return echoValue;
    }
    public void setEchoValue(String echoValue) {
        this.echoValue = echoValue;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
