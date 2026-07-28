package com.stubio.testUtil;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@XmlAccessorType(XmlAccessType.FIELD)
public class Endpoints {


    @XmlElement(name = "Endpoint", namespace = TstNamespace.URI)
    private List<Endpoint> endpoint = new ArrayList<>();

    public List<Endpoint> getEndpoint() {
        return endpoint;
    }

    /* Helper to get selected endpoint */
    public String getSelectedEndpoint() {
        return endpoint.stream()
                .filter(Endpoint::isSelected)
                .map(Endpoint::getValue)
                .findFirst()
                .orElse(endpoint.isEmpty() ? null : endpoint.get(0).getValue());
    }

    public void addIfAbsentAndSelect(String value) {
        if (value == null || value.isBlank()) return;

        Endpoint existing =
                endpoint.stream()
                        .filter(e -> value.equals(e.getValue()))
                        .findFirst()
                        .orElse(null);

        if (existing == null) {
            endpoint.add(new Endpoint(value, true));
        }

        // ensure exactly one selected="true"
        select(value);
    }

    public void select(String value) {
        endpoint.forEach(e -> {
            if (e.getValue() != null && e.getValue().equals(value)) {
                e.setSelected(Boolean.TRUE);
            } else {
                e.setSelected(null);    // removes attribute
            }
        });
    }

    public List<String> getEndpointValues() {
        return endpoint.stream()
                .map(Endpoint::getValue)
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.toList());
    }
}
