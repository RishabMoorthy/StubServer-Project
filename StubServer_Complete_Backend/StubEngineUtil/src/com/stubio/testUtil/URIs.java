package com.stubio.testUtil;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class URIs {

    @XmlElement(name = "URI", namespace = TstNamespace.URI)
    private List<UriEntry> uri = new ArrayList<>();

    public List<UriEntry> getUri() {
        return uri;
    }

    public String getSelectedUri() {
        return uri.stream()
                .filter(UriEntry::isSelected)
                .map(UriEntry::getValue)
                .findFirst()
                .orElse(uri.isEmpty() ? null : uri.get(0).getValue());
    }

    public void addIfAbsentAndSelect(String value) {
        if (value == null || value.isBlank()) return;

        UriEntry existing =
                uri.stream()
                        .filter(u -> value.equals(u.getValue()))
                        .findFirst()
                        .orElse(null);

        if (existing == null) {
            uri.add(new UriEntry(value, true));
        }

        select(value);
    }

    public void select(String value) {
        uri.forEach(u -> {
            if (u.getValue() != null && u.getValue().equals(value)) {
                u.setSelected(Boolean.TRUE);
            } else {
                u.setSelected(null);    // removes attribute
            }
        });
    }

    public List<String> getUriValues() {
        return uri.stream()
                .map(UriEntry::getValue)
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.toList());
    }
}
