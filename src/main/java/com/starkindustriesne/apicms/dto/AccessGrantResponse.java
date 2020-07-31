/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.starkindustriesne.apicms.domain.AccessGrantType;
import com.starkindustriesne.apicms.domain.Document;
import com.starkindustriesne.apicms.domain.Folder;
import java.util.Date;

/**
 *
 * @author michaelstark
 */
@JsonInclude(Include.NON_NULL)
public class AccessGrantResponse {
    private String clientKey;
    
    private DocumentResponse document;
    
    private FolderResponse folder;
    
    private AccessGrantType accessGrantType;

    private Date lastModified;

    private Date created;

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
     * @return the created
     */
    public Date getCreated() {
        return created;
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
     * @return the document
     */
    public DocumentResponse getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(DocumentResponse document) {
        this.document = document;
    }

    /**
     * @return the folder
     */
    public FolderResponse getFolder() {
        return folder;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(FolderResponse folder) {
        this.folder = folder;
    }
}
