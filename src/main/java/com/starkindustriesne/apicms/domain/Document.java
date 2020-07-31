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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author michaelstark
 */
@Entity
@Table(name="documents", indexes = {
    @Index(columnList="object_id", name="IDX_document_object_id")
})
public class Document implements Serializable {
    @Id
    @Column(name="document_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @JsonIgnore
    private long documentId = 0L;
    
    @Column(name="object_id", length = 60)
    private String objectId = UUID.randomUUID().toString();
    
    @JoinColumn(name="folder_id")
    @ManyToOne
    private Folder folder;
    
    @Column(name="name", length = 100)
    private String name;
    
    @Column(name="storage_url", length = 2048)
    @JsonIgnore
    private String storageUrl = "";
    
    @Column(name="last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified = new Date();
    
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    /**
     * @return the documentId
     */
    public long getDocumentId() {
        return documentId;
    }

    /**
     * @param documentId the documentId to set
     */
    public void setDocumentId(long documentId) {
        this.documentId = documentId;
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
     * @return the storageUrl
     */
    public String getStorageUrl() {
        return storageUrl;
    }

    /**
     * @param storageUrl the storageUrl to set
     */
    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
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
     * @return the folder
     */
    public Folder getFolder() {
        return folder;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(Folder folder) {
        this.folder = folder;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
