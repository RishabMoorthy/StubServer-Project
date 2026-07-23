package org.common.db.entity;

import jakarta.persistence.*;

/**
 * Services assigned to a user. Physical table name resolved centrally via
 * ConfigDrivenNamingStrategy (deployment-specific / dynamic table).
 */
@Entity
public class AssignedService {

    @EmbeddedId
    private AssignedServiceId id;

    public AssignedService() {
    }

    public AssignedService(String username, String serviceName) {
        this.id = new AssignedServiceId(username, serviceName);
    }

    public AssignedServiceId getId()            { return id; }
    public void setId(AssignedServiceId id)     { this.id = id; }
}
