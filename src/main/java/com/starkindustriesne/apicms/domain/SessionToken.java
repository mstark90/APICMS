/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author michaelstark
 */
@Entity
@Table(name="session_tokens", indexes = {
    @Index(columnList="session_token", name="IDX_sessiontokens_session_token")
})
public class SessionToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_token_id")
    private Long sessionTokenId = 0L;
    
    @Column(name="session_token", length = 60)
    private String sessionToken = UUID.randomUUID().toString();
    
    @Column(name="client_key", length = 60)
    private String clientKey;
    
    @Column(name = "expires_after")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAfter;
    
    @Column(name="last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified = new Date();
    
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();
    
    public SessionToken() {
        this.updateExpiryTimestamp();
    }
    
    public final void updateExpiryTimestamp() {
        Calendar calendar = Calendar.getInstance();
        
        calendar.add(Calendar.MINUTE, 5);
        
        expiresAfter = calendar.getTime();
    }

    /**
     * @return the sessionTokenId
     */
    public Long getSessionTokenId() {
        return sessionTokenId;
    }

    /**
     * @param sessionTokenId the sessionTokenId to set
     */
    public void setSessionTokenId(Long sessionTokenId) {
        this.sessionTokenId = sessionTokenId;
    }

    /**
     * @return the sessionToken
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * @param sessionToken the sessionToken to set
     */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    
    /**
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the expiresAfter
     */
    public Date getExpiresAfter() {
        return expiresAfter;
    }

    /**
     * @param expiresAfter the expiresAfter to set
     */
    public void setExpiresAfter(Date expiresAfter) {
        this.expiresAfter = expiresAfter;
    }

    /**
     * @return the clientKey
     */
    public String getClientKey() {
        return clientKey;
    }

    /**
     * @param clientKey the clientKey to set
     */
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }
}
