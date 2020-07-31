/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.components;

import com.starkindustriesne.apicms.UnauthorizedException;
import com.starkindustriesne.apicms.domain.AccessKey;
import com.starkindustriesne.apicms.repositories.AccessKeyRepository;
import com.starkindustriesne.apicms.util.RequestUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 *
 * @author michaelstark
 */
@Component
public class SecurityEngine {

    private final AccessKeyRepository accessKeyRepository;

    public SecurityEngine(AccessKeyRepository accessKeyRepository) {
        this.accessKeyRepository = accessKeyRepository;
    }

    @Before("execution(* com.starkindustriesne.apicms.controllers.*(..)")
    public void validateRequestSecurity(JoinPoint jp) {
        String clientKey = RequestUtil.getClientKey();
        String clientSecret = RequestUtil.getClientSecret();
        String sessionToken = RequestUtil.getSessionToken();

        AccessKey currentKey = this.accessKeyRepository
                .findByClientKeyIgnoreCase(clientKey);

        if (currentKey == null
                || !currentKey.getClientSecret().equalsIgnoreCase(clientSecret)) {
            throw new UnauthorizedException();
        }
        
        RequestUtil.getRequest().setAttribute("accessKey", currentKey);
    }
}
