/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.components;

import com.starkindustriesne.apicms.UnauthorizedException;
import com.starkindustriesne.apicms.domain.AccessKey;
import com.starkindustriesne.apicms.domain.SessionToken;
import com.starkindustriesne.apicms.repositories.AccessKeyRepository;
import com.starkindustriesne.apicms.repositories.SessionTokenRepository;
import com.starkindustriesne.apicms.util.RequestUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 *
 * @author michaelstark
 */
@Component
@Aspect
public class SecurityEngine {

    private final AccessKeyRepository accessKeyRepository;
    private final SessionTokenRepository sessionTokenRepository;

    public SecurityEngine(AccessKeyRepository accessKeyRepository,
            SessionTokenRepository sessionTokenRepository) {
        this.accessKeyRepository = accessKeyRepository;
        this.sessionTokenRepository = sessionTokenRepository;
    }

    @Before("execution(public * com.starkindustriesne.apicms.controllers.*.*(..))")
    public void validateRequestSecurity(JoinPoint jp) {
        String clientKey = RequestUtil.getClientKey();
        String clientSecret = RequestUtil.getClientSecret();
        String sessionTokenId = RequestUtil.getSessionToken();

        SessionToken sessionToken = this.sessionTokenRepository.
                findValidSessionToken(sessionTokenId);
        AccessKey currentKey = null;

        if (sessionToken != null) {
            currentKey = this.accessKeyRepository
                    .findByClientKeyIgnoreCase(sessionToken.getClientKey());
            
            if (currentKey == null || !currentKey.getClientKey()
                    .equalsIgnoreCase(clientKey)) {
                throw new UnauthorizedException();
            } else {
                
            }
        } else {
            currentKey = this.accessKeyRepository
                    .findByClientKeyIgnoreCase(clientKey);

            if (currentKey == null || !currentKey.getClientSecret()
                    .equalsIgnoreCase(clientSecret)) {
                throw new UnauthorizedException();
            }
        }

        RequestUtil.getRequest().setAttribute("accessKey", currentKey);
    }
}
