package com.stubio.util;

import java.util.Objects;

public class GenericProperty {

    public GenericProperty() {
    }

    public GenericProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    String key, value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof GenericProperty))
            return false;
        GenericProperty that = (GenericProperty) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "GenericProperty{" + "key='" + key + '\'' + ", value='" + value + '\'' + '}';
    }
}
