package com.stubio.testUtil;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class UriEntry {

    @XmlAttribute
    private Boolean selected;

    @XmlValue
    private String value;

    public UriEntry() {}

    public UriEntry(String value, boolean selected) {
        this.value = value;
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public Boolean isSelected() {
        return selected != null && selected;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
