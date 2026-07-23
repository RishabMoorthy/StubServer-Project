package org.common.db.entity;

import jakarta.persistence.*;

/**
 * READYAPI_MONTHLY_METRICS — aggregated monthly hit counts.
 * Physical table name resolved centrally via ConfigDrivenNamingStrategy.
 */
@Entity
public class MonthlyMetrics {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "VSNAME")
    private String vsname;

    @Column(name = "MONTH")
    private String month;

    @Column(name = "YEAR")
    private String year;

    @Column(name = "COUNT")
    private Long count;

    @Column(name = "QACOUNT")
    private Long qaCount;

    @Column(name = "PERFCOUNT")
    private Long perfCount;

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public String getVsname()                   { return vsname; }
    public void setVsname(String vsname)        { this.vsname = vsname; }
    public String getMonth()                    { return month; }
    public void setMonth(String month)          { this.month = month; }
    public String getYear()                     { return year; }
    public void setYear(String year)            { this.year = year; }
    public Long getCount()                      { return count; }
    public void setCount(Long count)            { this.count = count; }
    public Long getQaCount()                    { return qaCount; }
    public void setQaCount(Long qaCount)        { this.qaCount = qaCount; }
    public Long getPerfCount()                  { return perfCount; }
    public void setPerfCount(Long perfCount)    { this.perfCount = perfCount; }
}
