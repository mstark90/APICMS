/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.services;

import com.starkindustriesne.apicms.UnauthorizedException;
import com.starkindustriesne.apicms.domain.AccessGrant;
import com.starkindustriesne.apicms.domain.AccessGrantType;
import com.starkindustriesne.apicms.domain.AccessKey;
import com.starkindustriesne.apicms.repositories.AccessGrantRepository;
import com.starkindustriesne.apicms.repositories.AccessKeyRepository;
import com.starkindustriesne.apicms.util.RequestUtil;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 *
 * @author michaelstark
 */
@Service
public class AccessGrantService {

    private final AccessGrantRepository accessGrantRepository;

    private final AccessKeyRepository accessKeyRepository;

    public AccessGrantService(AccessGrantRepository accessGrantRepository,
            AccessKeyRepository accessKeyRepository) {
        this.accessGrantRepository = accessGrantRepository;

        this.accessKeyRepository = accessKeyRepository;
    }
    
    private void checkIsAdmin() {
        String clientKey = RequestUtil.getClientKey();
        String clientSecret = RequestUtil.getClientSecret();
        
        if (clientKey != null && !clientKey.isBlank()) {
            AccessKey currentKey = this.accessKeyRepository
                    .findByClientKeyIgnoreCase(clientKey);

            if (currentKey == null || !currentKey.getClientSecret()
                    .equalsIgnoreCase(clientSecret) || !currentKey.isIsAdmin()) {
                throw new UnauthorizedException();
            }
        }
    }

    public boolean hasAdminAccess(String objectId) {
        List<AccessGrantType> accessGrants = Arrays.asList(AccessGrantType.ADMIN,
                AccessGrantType.CREATOR);
        
        return this.hasAccess(RequestUtil.getClientKey(), objectId,
                accessGrants);
    }

    public boolean hasWriteAccess(String objectId) {
        List<AccessGrantType> accessGrants = Arrays.asList(AccessGrantType.ADMIN,
                AccessGrantType.CREATOR, AccessGrantType.WRITE);
        
        return this.hasAccess(RequestUtil.getClientKey(), objectId,
                accessGrants);
    }

    public boolean hasReadAccess(String objectId) {
        List<AccessGrantType> accessGrants = Arrays.asList(AccessGrantType.ADMIN,
                AccessGrantType.CREATOR, AccessGrantType.WRITE,
                AccessGrantType.READ);
        
        return this.hasAccess(RequestUtil.getClientKey(), objectId,
                accessGrants);
    }
    
    private boolean hasAccess(String clientKey, String objectId,
            Iterable<AccessGrantType> accessGrantTypes) {
        List<AccessGrant> grants = this.accessGrantRepository
                .findByObjectIdIgnoreCaseAndClientKeyIgnoreCaseAndAccessGrantTypeIn(objectId,
                        RequestUtil.getClientKey(), accessGrantTypes);
        AccessKey key = this.accessKeyRepository.findByClientKeyIgnoreCase(
                RequestUtil.getClientKey());

        return key != null && key.getClientSecret().equalsIgnoreCase(
                RequestUtil.getClientSecret()) && (!grants.isEmpty() || key.isIsAdmin());
    }

    public boolean hasAccess(String objectId, AccessGrantType accessGrantType) {
        return hasAccess(RequestUtil.getClientKey(), objectId, accessGrantType);
    }

    public boolean hasAccess(String clientKey, String objectId,
            AccessGrantType accessGrantType) {
        return this.hasAccess(RequestUtil.getClientKey(), objectId,
                Arrays.asList(accessGrantType));
    }

    public AccessGrant create(String creatorClientKey,
            String clientKey, String objectId, AccessGrantType accessGrantType) {
        this.checkHasGrantPower(creatorClientKey, objectId);

        AccessGrant grant = this.accessGrantRepository
                .findByObjectIdIgnoreCaseAndClientKeyIgnoreCase(objectId, clientKey);

        if (grant == null) {
            grant = new AccessGrant();

            grant.setCreated(new Date());
            grant.setClientKey(clientKey);
            grant.setObjectId(objectId);
        }

        grant.setAccessGrantType(accessGrantType);
        grant.setLastModified(new Date());

        grant = this.accessGrantRepository.save(grant);

        return grant;
    }
    
    public List<AccessGrant> getObjectAccessGrants(String objectId) {
        return this.getObjectAccessGrants(RequestUtil.getClientKey(), objectId);
    }

    public List<AccessGrant> getObjectAccessGrants(String clientKey,
            String objectId) {
        this.checkHasGrantPower(clientKey, objectId);

        return this.accessGrantRepository.findAllByObjectId(objectId);
    }
    
    public List<AccessGrant> getGrantsForClientKey(String clientKey) {
        this.checkIsAdmin();
        
        return this.accessGrantRepository.findAllByClientKey(clientKey);
    }

    protected void checkHasGrantPower(String clientKey, String objectId) {
        if (!this.hasAccess(clientKey, objectId, AccessGrantType.ADMIN)
                && this.hasAccess(clientKey, objectId, AccessGrantType.CREATOR)) {
            throw new UnauthorizedException();
        }
    }

    public void createGrants(String objectId, String parentObjectId) {
        this.createGrants(objectId, parentObjectId, RequestUtil.getClientKey());
    }

    public void createGrants(String objectId,
            String parentObjectId,
            String clientKey) {
        if (parentObjectId != null) {
            List<AccessGrant> accessGrants = this.accessGrantRepository
                    .findAllByObjectId(parentObjectId);

            accessGrants = accessGrants.stream()
                    .filter(accessGrant -> !clientKey.equalsIgnoreCase(accessGrant.getClientKey()))
                    .map(accessGrant -> {
                AccessGrant newGrant = new AccessGrant();
                AccessGrantType accessGrantType = accessGrant
                        .getAccessGrantType();

                if (accessGrantType == AccessGrantType.CREATOR) {
                    accessGrantType = AccessGrantType.ADMIN;
                }

                newGrant.setObjectId(objectId);
                newGrant.setClientKey(accessGrant.getClientKey());
                newGrant.setAccessGrantType(accessGrantType);

                return newGrant;
            }).collect(Collectors.toList());

            this.accessGrantRepository.saveAll(accessGrants);
        }

        AccessKey key = this.accessKeyRepository.findByClientKeyIgnoreCase(clientKey);

        if (key == null || (parentObjectId == null && !key.isIsAdmin())) {
            throw new UnauthorizedException();
        }

        AccessGrant accessGrant = new AccessGrant();

        accessGrant.setAccessGrantType(AccessGrantType.CREATOR);
        accessGrant.setClientKey(clientKey);
        accessGrant.setObjectId(objectId);

        this.accessGrantRepository.save(accessGrant);

    }
}
