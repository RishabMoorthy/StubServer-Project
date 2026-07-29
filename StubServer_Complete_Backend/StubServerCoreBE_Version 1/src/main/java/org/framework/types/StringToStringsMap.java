package org.framework.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringToStringsMap {
    private final Map<String, List<String>> map;

    public StringToStringsMap() {
        this.map = new HashMap<>();
    }

    public void putValue(String key, String value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public void put(String key, List<String> list) {
        map.put(key, list);
    }

    public List<String> get(String key) {
        return map.getOrDefault(key, new ArrayList<>());
    }

    public void removeKey(String key) {
        map.remove(key);
    }

    public Map<String, List<String>> getMap() {
        return this.map;
    }
}
