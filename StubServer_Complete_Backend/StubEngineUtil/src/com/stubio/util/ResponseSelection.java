package com.stubio.util;

public class ResponseSelection {

    public ResponseSelection(){}
    public ResponseSelection(ResponseSelection rs)
    {
        this.matchScript = rs.getMatchScript();
        this.matchStyle = rs.getMatchStyle();
    }
    private String matchStyle;
    private Script matchScript;

    public String getMatchStyle() {
        return matchStyle;
    }
    public void setMatchStyle(String matchStyle) {
        this.matchStyle = matchStyle;
    }

    public Script getMatchScript() {
        return matchScript;
    }
    public void setMatchScript(Script matchScript) {
        this.matchScript = matchScript;
    }
}
