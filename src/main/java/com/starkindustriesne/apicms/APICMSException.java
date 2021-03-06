/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author michaelstark
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class APICMSException extends RuntimeException {
    public APICMSException(String message) {
        super(message);
    }
    
    public APICMSException(String message, Exception innerException) {
        super(message, innerException);
    }
    
    public APICMSException(Exception innerException) {
        super(innerException);
    }
}
