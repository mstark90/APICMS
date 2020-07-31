/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author michaelstark
 */
@Entity
@Table(name="folders", indexes = {
    @Index(columnList="object_id", name="IDX_folder_object_id")
})
public class Folder {
    @Id
    @Column(name="folder_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @JsonIgnore
    private Long folderId = 0L;
    
    @JoinTable(name = "folder_tree",
            joinColumns = {
                @JoinColumn(name = "parent_folder_id", referencedColumnName = "folder_id")
            },
            inverseJoinColumns = {
                @JoinColumn(name="child_folder_id", referencedColumnName="folder_id")
            })
    @ManyToOne
    private Folder parent;
    
    @Column(name="object_id", length = 60)
    private String objectId = UUID.randomUUID().toString();
    
    @Column(name="name", length = 100)
    private String name;
    
    @Column(name="last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified = new Date();
    
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    /**
     * @return the folderId
     */
    public long getFolderId() {
        return folderId;
    }

    /**
     * @param folderId the folderId to set
     */
    public void setFolderId(long folderId) {
        this.folderId = folderId;
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
     * @return the parent
     */
    public Folder getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Folder parent) {
        this.parent = parent;
    }
}
