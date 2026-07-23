package org.common.db.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * AUTH_REFRESH_TOKENS — persisted JWT refresh tokens.
 * Physical table name resolved centrally via ConfigDrivenNamingStrategy.
 */
@Entity
public class RefreshToken {

    @Id
    @Column(name = "JTI")
    private String jti;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "EXPIRES_AT")
    private Timestamp expiresAt;

    @Column(name = "ISSUED_AT")
    private Timestamp issuedAt;

    @Column(name = "USER_AGENT")
    private String userAgent;

    @Column(name = "IP")
    private String ip;

    public String getJti()                      { return jti; }
    public void setJti(String jti)              { this.jti = jti; }
    public String getUsername()                 { return username; }
    public void setUsername(String username)    { this.username = username; }
    public Timestamp getExpiresAt()             { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }
    public Timestamp getIssuedAt()              { return issuedAt; }
    public void setIssuedAt(Timestamp issuedAt) { this.issuedAt = issuedAt; }
    public String getUserAgent()                { return userAgent; }
    public void setUserAgent(String userAgent)  { this.userAgent = userAgent; }
    public String getIp()                       { return ip; }
    public void setIp(String ip)                { this.ip = ip; }
}
