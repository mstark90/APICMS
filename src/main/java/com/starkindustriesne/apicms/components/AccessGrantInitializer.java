/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.components;

import com.starkindustriesne.apicms.domain.AccessGrantType;
import com.starkindustriesne.apicms.domain.AccessKey;
import com.starkindustriesne.apicms.dto.CreateAccessKeyRequest;
import com.starkindustriesne.apicms.services.AccessGrantService;
import com.starkindustriesne.apicms.services.AccessKeyService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 *
 * @author michaelstark
 */
@Component
public class AccessGrantInitializer {
    private final AccessGrantService accessGrantService;
    private final AccessKeyService accessKeyService;
    
    private static final Logger logger =
            Logger.getLogger(AccessGrantInitializer.class.getName());
    
    public AccessGrantInitializer(AccessGrantService accessGrantService,
            AccessKeyService accessKeyService) {
        this.accessGrantService = accessGrantService;
        this.accessKeyService = accessKeyService;
    }
    
    @PostConstruct
    protected void loadAdminGrant() {
        if(this.accessKeyService.getKeys(false).isEmpty()) {
            AccessKey key = this.accessKeyService.createAdmin("");
            
            logger.log(Level.INFO, "Created initial client key: {0}",
                    key.getClientKey());
            logger.log(Level.INFO, "Created initial client secret: {0}",
                    key.getClientSecret());
            
            logger.log(Level.INFO,
                    "It is recommended that you save the above key and restart APICMS.");
        }
    }
}
