/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.repositories;

import com.starkindustriesne.apicms.domain.SessionToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author michaelstark
 */
@Repository
public interface SessionTokenRepository extends CrudRepository<SessionToken, Long> {
    SessionToken findBySessionToken(String sessionToken);
    
    @Query("SELECT st FROM SessionToken st WHERE st.sessionToken = ?1 AND st.expiresAfter < NOW()")
    SessionToken findValidSessionToken(String sessionToken);
}
