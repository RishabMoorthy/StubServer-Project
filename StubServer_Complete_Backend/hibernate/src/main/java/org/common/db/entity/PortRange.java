package org.common.db.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * READYAPI_PORT_RANGE — application/port assignments.
 * Physical table name resolved centrally via ConfigDrivenNamingStrategy.
 */
@Entity
public class PortRange {

    @Id
    @Column(name = "PORTID")
    private Long portId;

    @Column(name = "APPNAME")
    private String appName;

    @Column(name = "PORTS")
    private String ports;

    @Column(name = "UPDATEDBY")
    private String updatedBy;

    @Column(name = "UPDATETIME")
    private Timestamp updateTime;

    public Long getPortId()                     { return portId; }
    public void setPortId(Long portId)          { this.portId = portId; }
    public String getAppName()                  { return appName; }
    public void setAppName(String appName)      { this.appName = appName; }
    public String getPorts()                    { return ports; }
    public void setPorts(String ports)          { this.ports = ports; }
    public String getUpdatedBy()                { return updatedBy; }
    public void setUpdatedBy(String updatedBy)  { this.updatedBy = updatedBy; }
    public Timestamp getUpdateTime()            { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
}
