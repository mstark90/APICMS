/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.repositories;

import com.starkindustriesne.apicms.domain.EventHook;
import com.starkindustriesne.apicms.domain.EventType;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author michaelstark
 */
@Repository
public interface EventHookRepository extends CrudRepository<EventHook, Long> {
    List<EventHook> findByParentObjectId(String parentObjectId);
    List<EventHook> findByParentObjectIdAndEventType(String parentObjectId,
            EventType eventType);
}
