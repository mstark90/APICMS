/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
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
@Table(name="event_hooks", indexes = {
    @Index(columnList="parent_object_id", name="IDX_eventhooks_parent_object_id")
})
public class EventHook implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_hook_id")
    private Long eventHookId = 0L;
    
    @Column(name = "creator", length = 60)
    private String creator;
    
    @Column(name = "parent_object_id", length = 60)
    private String parentObjectId;
    
    @Column(name = "client_key", length = 60)
    private String clientKey;

    @Column(name = "handler_url", length = 2048)
    private String handlerUrl;
    
    @Column(name = "event_name", length = 150)
    private String eventName;
    
    @Column(name = "event_type")
    @Enumerated(EnumType.ORDINAL)
    private EventType eventType;

    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified = new Date();

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    /**
     * @return the eventHookId
     */
    public Long getEventHookId() {
        return eventHookId;
    }

    /**
     * @param eventHookId the eventHookId to set
     */
    public void setEventHookId(Long eventHookId) {
        this.eventHookId = eventHookId;
    }

    /**
     * @return the parentObjectId
     */
    public String getParentObjectId() {
        return parentObjectId;
    }

    /**
     * @param parentObjectId the parentObjectId to set
     */
    public void setParentObjectId(String parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    /**
     * @return the handlerUrl
     */
    public String getHandlerUrl() {
        return handlerUrl;
    }

    /**
     * @param handlerUrl the handlerUrl to set
     */
    public void setHandlerUrl(String handlerUrl) {
        this.handlerUrl = handlerUrl;
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
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName the eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

}
