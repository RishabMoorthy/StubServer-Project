package org.common.db.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * READYAPI_RESPONSE_TIME — per-window response-time samples.
 * Physical table name resolved centrally via ConfigDrivenNamingStrategy.
 */
@Entity
public class ResponseTime {

    @Id
    @Column(name = "RESPID")
    private Long respId;

    @Column(name = "METRICSID")
    private Long metricsId;

    @Column(name = "STARTTIME")
    private Timestamp startTime;

    @Column(name = "ENDTIME")
    private Timestamp endTime;

    @Column(name = "AVGRESPTIME")
    private Double avgRespTime;

    @Column(name = "MAXRESPTIME")
    private Double maxRespTime;

    @Column(name = "AVGTPS")
    private Double avgTps;

    public Long getRespId()                     { return respId; }
    public void setRespId(Long respId)          { this.respId = respId; }
    public Long getMetricsId()                  { return metricsId; }
    public void setMetricsId(Long metricsId)    { this.metricsId = metricsId; }
    public Timestamp getStartTime()             { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
    public Timestamp getEndTime()               { return endTime; }
    public void setEndTime(Timestamp endTime)   { this.endTime = endTime; }
    public Double getAvgRespTime()              { return avgRespTime; }
    public void setAvgRespTime(Double avgRespTime) { this.avgRespTime = avgRespTime; }
    public Double getMaxRespTime()              { return maxRespTime; }
    public void setMaxRespTime(Double maxRespTime) { this.maxRespTime = maxRespTime; }
    public Double getAvgTps()                   { return avgTps; }
    public void setAvgTps(Double avgTps)        { this.avgTps = avgTps; }
}
