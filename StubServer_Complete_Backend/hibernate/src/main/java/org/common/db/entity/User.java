package org.common.db.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * STUBSERVERUSERS — portal user accounts.
 * Physical table name resolved centrally via ConfigDrivenNamingStrategy.
 */
@Entity
public class User {

    @Id
    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FIRSTNAME")
    private String firstname;

    @Column(name = "LASTNAME")
    private String lastname;

    @Column(name = "USERROLE")
    private String userrole;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    public String getUsername()                 { return username; }
    public void setUsername(String username)    { this.username = username; }
    public String getPassword()                 { return password; }
    public void setPassword(String password)    { this.password = password; }
    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }
    public String getFirstname()                { return firstname; }
    public void setFirstname(String firstname)  { this.firstname = firstname; }
    public String getLastname()                 { return lastname; }
    public void setLastname(String lastname)    { this.lastname = lastname; }
    public String getUserrole()                 { return userrole; }
    public void setUserrole(String userrole)    { this.userrole = userrole; }
    public String getCreatedBy()                { return createdBy; }
    public void setCreatedBy(String createdBy)  { this.createdBy = createdBy; }
    public String getUpdatedBy()                { return updatedBy; }
    public void setUpdatedBy(String updatedBy)  { this.updatedBy = updatedBy; }
    public Timestamp getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
