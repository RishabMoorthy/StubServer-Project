package org.common.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key for AssignedService (USERNAME + SERVICENAME).
 */
@Embeddable
public class AssignedServiceId implements Serializable {

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "SERVICENAME")
    private String serviceName;

    public AssignedServiceId() {
    }

    public AssignedServiceId(String username, String serviceName) {
        this.username = username;
        this.serviceName = serviceName;
    }

    public String getUsername()                 { return username; }
    public void setUsername(String username)    { this.username = username; }
    public String getServiceName()              { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssignedServiceId that)) return false;
        return Objects.equals(username, that.username)
                && Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, serviceName);
    }
}
