/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
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
@Table(name = "access_keys", indexes = {
    @Index(columnList="client_key", name="IDX_accesskey_clientkey"),
    @Index(columnList="client_secret", name="IDX_accesskey_clientsecret")
})
public class AccessKey implements Serializable {

    @Id
    @Column(name = "access_key_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long accessKeyId = 0L;
    
    @Column(name="creator", length = 60)
    @JsonIgnore
    private String creator;
    
    @Column(name="client_key", length = 60)
    private String clientKey = UUID.randomUUID().toString();
    
    @Column(name="client_secret", length = 60)
    @JsonIgnore
    private String clientSecret = UUID.randomUUID().toString();
    
    @Column(name = "key_name", length = 100)
    private String keyName;
    
    @Column(name="is_admin")
    private boolean isAdmin = false;
    
    @Column(name="last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified = new Date();
    
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    /**
     * @return the accessKeyId
     */
    public long getAccessKeyId() {
        return accessKeyId;
    }

    /**
     * @param accessKeyId the accessKeyId to set
     */
    public void setAccessKeyId(long accessKeyId) {
        this.accessKeyId = accessKeyId;
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
     * @return the clientSecret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret the clientSecret to set
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * @return the isAdmin
     */
    public boolean isIsAdmin() {
        return isAdmin;
    }

    /**
     * @param isAdmin the isAdmin to set
     */
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
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
     * @return the keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * @param keyName the keyName to set
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

}
