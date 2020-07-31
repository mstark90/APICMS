/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.dto;

import com.starkindustriesne.apicms.domain.AccessGrantType;

/**
 *
 * @author michaelstark
 */
public class CreateAccessGrantRequest {
    private String clientKey;
    
    private AccessGrantType accessGrantType;

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
}
