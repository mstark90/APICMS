/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
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
@Table(name="access_grants", indexes = {
    @Index(columnList="object_id", name="IDX_accessgrants_object_id")
})
public class AccessGrant implements Serializable {
    @Id
    @Column(name="access_grant_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @JsonIgnore
    private long accessGrantId = 0L;
    
    @Column(name="object_id", length = 60)
    private String objectId;
    
    @Column(name="client_key", length = 60)
    private String clientKey;
    
    @Column(name="access_grant_type")
    @Enumerated()
    private AccessGrantType accessGrantType;
    
    @Column(name="last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified = new Date();
    
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    /**
     * @return the accessGrantId
     */
    public long getAccessGrantId() {
        return accessGrantId;
    }

    /**
     * @param accessGrantId the accessGrantId to set
     */
    public void setAccessGrantId(long accessGrantId) {
        this.accessGrantId = accessGrantId;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @param objectId the objectId to set
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    /**
     * @return the accessGrantType
     */
    public AccessGrantType getAccessGrantType() {
        return accessGrantType;
    }

    /**
     * @param accessGrantType the accessGrantType to set
     */
    public void setAccessGrantType(AccessGrantType accessGrantType) {
        this.accessGrantType = accessGrantType;
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
}
