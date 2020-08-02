/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.services;

import com.starkindustriesne.apicms.UnauthorizedException;
import com.starkindustriesne.apicms.domain.AccessKey;
import com.starkindustriesne.apicms.dto.CreateAccessKeyRequest;
import com.starkindustriesne.apicms.repositories.AccessKeyRepository;
import com.starkindustriesne.apicms.util.RequestUtil;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author michaelstark
 */
@Service
public class AccessKeyService {

    private final AccessKeyRepository accessKeyRepository;

    public AccessKeyService(AccessKeyRepository accessKeyRepository) {
        this.accessKeyRepository = accessKeyRepository;
    }
    
    private void checkIsAdmin() {
        this.checkIsAdmin(RequestUtil.getClientKey());
    }
    
    private void checkIsAdmin(String clientKey) {
        if (clientKey != null && !clientKey.isBlank()) {
            String clientSecret = RequestUtil.getClientSecret();
            AccessKey currentKey = this.accessKeyRepository
                    .findByClientKeyIgnoreCase(clientKey);

            if (currentKey == null || !currentKey.getClientSecret()
                    .equalsIgnoreCase(clientSecret) || !currentKey.isIsAdmin()) {
                throw new UnauthorizedException();
            }
        }
        
        if(RequestUtil.getSessionToken() != null) {
            throw new UnauthorizedException();
        }
    }

    public AccessKey create(CreateAccessKeyRequest request) {
        this.checkIsAdmin();

        AccessKey key = new AccessKey();
        key.setCreator(RequestUtil.getClientKey());
        key.setCreated(new Date());
        key.setLastModified(new Date());
        key.setKeyName(request.getKeyName());
        key.setIsAdmin(request.isAdmin());

        key = this.accessKeyRepository.save(key);

        return key;
    }
    
    public AccessKey createEventKey(String eventHookName, String objectName) {
        AccessKey key = new AccessKey();
        key.setCreator(RequestUtil.getClientKey());
        key.setCreated(new Date());
        key.setLastModified(new Date());
        key.setKeyName(String.format("%s - %s", eventHookName, objectName));
        key.setIsAdmin(false);

        key = this.accessKeyRepository.save(key);

        return key;
    }

    public AccessKey createAdmin() {
        return this.createAdmin(RequestUtil.getClientKey());
    }

    public AccessKey createAdmin(String clientKey) {

        this.checkIsAdmin(clientKey);

        AccessKey key = new AccessKey();
        key.setCreator(clientKey);
        key.setCreated(new Date());
        key.setLastModified(new Date());
        key.setKeyName("Admin");
        key.setIsAdmin(true);

        key = this.accessKeyRepository.save(key);

        return key;
    }
    
    public AccessKey getByClientKey(String clientKey) {
        return this.accessKeyRepository.findByClientKeyIgnoreCase(clientKey);
    }

    public List<AccessKey> getKeys() {
        return this.getKeys(true);
    }

    public List<AccessKey> getKeys(boolean checkAdmin) {
        if (checkAdmin) {
            this.checkIsAdmin();
        }

        return this.accessKeyRepository.findAll();
    }
}
