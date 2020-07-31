/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author michaelstark
 */
@Entity
@Table(name="folder_properties")
public class FolderProperty implements Serializable {
    @Id
    @Column(name="folder_property_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long folderPropertyId = 0L;
    
    @Column(name="folder_id")
    private Long folderId = 0L;
    
    @Column(name = "property_name", length = 75)
    private String propertyName;
    
    @Column(name = "property_value", length = 2048)
    private String propertyValue;

    /**
     * @return the folderPropertyId
     */
    public long getFolderPropertyId() {
        return folderPropertyId;
    }

    /**
     * @param folderPropertyId the folderPropertyId to set
     */
    public void setFolderPropertyId(long folderPropertyId) {
        this.folderPropertyId = folderPropertyId;
    }

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
     * @return the propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @param propertyName the propertyName to set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @return the propertyValue
     */
    public String getPropertyValue() {
        return propertyValue;
    }

    /**
     * @param propertyValue the propertyValue to set
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
