/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.dto;

import com.starkindustriesne.apicms.domain.EventType;

/**
 *
 * @author michaelstark
 */
public class ModifyEventHookRequest {
    private long eventHookId = 0;
    
    private String appClientKey;
    
    private String objectId;
    
    private String handlerUrl;
    
    private String eventName;
    
    private EventType eventType;

    /**
     * @return the appClientKey
     */
    public String getAppClientKey() {
        return appClientKey;
    }

    /**
     * @param appClientKey the appClientKey to set
     */
    public void setAppClientKey(String appClientKey) {
        this.appClientKey = appClientKey;
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
     * @return the eventHookId
     */
    public long getEventHookId() {
        return eventHookId;
    }

    /**
     * @param eventHookId the eventHookId to set
     */
    public void setEventHookId(long eventHookId) {
        this.eventHookId = eventHookId;
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
