package org.common.db.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * Audit log entries. Physical table name resolved centrally via
 * ConfigDrivenNamingStrategy (deployment-specific / dynamic table).
 */
@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "ACTION_TYPE")
    private String actionType;

    @Column(name = "REMARK")
    private String remark;

    // Populated automatically by the database — not set on insert
    @Column(name = "TIMESTAMP", insertable = false, updatable = false)
    private Timestamp timestamp;

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public String getUsername()                 { return username; }
    public void setUsername(String username)    { this.username = username; }
    public String getServiceName()              { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getActionType()               { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getRemark()                   { return remark; }
    public void setRemark(String remark)        { this.remark = remark; }
    public Timestamp getTimestamp()             { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
