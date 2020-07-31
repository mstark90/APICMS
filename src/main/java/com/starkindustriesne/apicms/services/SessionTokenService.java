/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.services;

import com.starkindustriesne.apicms.domain.SessionToken;
import com.starkindustriesne.apicms.repositories.SessionTokenRepository;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author michaelstark
 */
@Service
public class SessionTokenService {
    private final SessionTokenRepository sessionTokenRepository;
    private final Logger logger = Logger.getLogger(SessionTokenService.class.getName());
    
    public SessionTokenService(SessionTokenRepository sessionTokenRepository) {
        this.sessionTokenRepository = sessionTokenRepository;
    }
    
    public SessionToken getSessionToken(String sessionToken) {
        return this.getSessionToken(sessionToken, true);
    }
    
    public SessionToken getSessionToken(String sessionToken,
            boolean updateValidity) {
        SessionToken token = this.sessionTokenRepository
                .findValidSessionToken(sessionToken);
        
        if(token != null && updateValidity) {
            logger.log(Level.INFO, "Found valid token. Updating validity.");
            
            token.updateExpiryTimestamp();
            
            token = this.sessionTokenRepository.save(token);
            
            logger.log(Level.INFO, "Token validity updated.");
        }
        
        return token;
    }
    
    public SessionToken create(String clientKey) {
        logger.log(Level.INFO, "Creating session token for {0}.", clientKey);
        SessionToken sessionToken = new SessionToken();
        
        sessionToken.setClientKey(clientKey);
        
        sessionToken = this.sessionTokenRepository.save(sessionToken);
        
        logger.log(Level.INFO, "Created session token for {0}.", clientKey);
        
        return sessionToken;
    }
}
