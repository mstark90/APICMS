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
@Table(name="document_properties")
public class DocumentProperty implements Serializable {
    @Id
    @Column(name="document_property_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long documentPropertyId = 0L;
    
    @Column(name="document_id")
    private Long documentId = 0L;
    
    @Column(name = "property_name", length = 75)
    private String propertyName;
    
    @Column(name = "property_value", length = 2048)
    private String propertyValue;

    /**
     * @return the documentPropertyId
     */
    public long getDocumentPropertyId() {
        return documentPropertyId;
    }

    /**
     * @param documentPropertyId the documentPropertyId to set
     */
    public void setDocumentPropertyId(long documentPropertyId) {
        this.documentPropertyId = documentPropertyId;
    }

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
