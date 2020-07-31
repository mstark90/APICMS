/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.repositories;

import com.starkindustriesne.apicms.domain.AccessGrant;
import com.starkindustriesne.apicms.domain.AccessGrantType;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author michaelstark
 */
@Repository
public interface AccessGrantRepository
        extends CrudRepository<AccessGrant, Long> {
    List<AccessGrant> findAllByObjectId(String objectId);
    List<AccessGrant> findAllByClientKey(String objectId);
    
    AccessGrant findByObjectIdIgnoreCaseAndClientKeyIgnoreCaseAndAccessGrantType(String objectId,
            String clientKey, AccessGrantType accessGrantType);
    List<AccessGrant> findByObjectIdIgnoreCaseAndClientKeyIgnoreCaseAndAccessGrantTypeIn(String objectId,
            String clientKey, Iterable<AccessGrantType> accessGrantType);
    AccessGrant findByObjectIdIgnoreCaseAndClientKeyIgnoreCase(String objectId, String clientKey);
}
