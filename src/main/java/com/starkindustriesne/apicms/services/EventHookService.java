/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.services;

import com.starkindustriesne.apicms.BadRequestException;
import com.starkindustriesne.apicms.UnauthorizedException;
import com.starkindustriesne.apicms.domain.AccessGrantType;
import com.starkindustriesne.apicms.domain.AccessKey;
import com.starkindustriesne.apicms.domain.Document;
import com.starkindustriesne.apicms.domain.EventHook;
import com.starkindustriesne.apicms.domain.EventType;
import com.starkindustriesne.apicms.domain.SessionToken;
import com.starkindustriesne.apicms.dto.ModifyEventHookRequest;
import com.starkindustriesne.apicms.repositories.EventHookRepository;
import com.starkindustriesne.apicms.util.RequestUtil;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author michaelstark
 */
@Service
public class EventHookService {

    private final CloseableHttpClient httpClient;
    private final EventHookRepository eventHookRepository;
    private final AccessGrantService accessGrantService;
    private final AccessKeyService accessKeyService;
    private final SessionTokenService sessionTokenService;

    private final Logger logger = Logger.getLogger(EventHookService.class.getName());

    public EventHookService(EventHookRepository eventHookRepository,
            AccessGrantService accessGrantService,
            AccessKeyService accessKeyService,
            SessionTokenService sessionTokenService) {
        this.eventHookRepository = eventHookRepository;
        this.accessGrantService = accessGrantService;
        this.accessKeyService = accessKeyService;
        this.sessionTokenService = sessionTokenService;

        this.httpClient = HttpClientBuilder.create().build();
    }

    @PreDestroy
    public void dispose() throws IOException {
        this.httpClient.close();
    }

    public EventHook modify(ModifyEventHookRequest request) {
        if(request.getObjectId() == null) {
            throw new BadRequestException();
        }
        
        if (!this.accessGrantService.hasAdminAccess(request.getObjectId())) {
            throw new UnauthorizedException();
        }

        logger.log(Level.INFO, "Updating event hooks for {0}.",
                request.getObjectId());

        EventHook eventHook = new EventHook();
        
        if(request.getAppClientKey() == null) {
            AccessKey key = this.accessKeyService.createEventKey(
                    request.getEventName(), request.getObjectId());
            
            this.accessGrantService.create(RequestUtil.getClientKey(), 
                    key.getClientKey(), request.getObjectId(),
                    AccessGrantType.WRITE);
            
            eventHook.setClientKey(key.getClientKey());
        } else {
            eventHook.setClientKey(request.getAppClientKey());
        }
        
        eventHook.setEventName(request.getEventName());
        eventHook.setCreator(RequestUtil.getClientKey());
        eventHook.setHandlerUrl(request.getHandlerUrl());
        eventHook.setParentObjectId(request.getObjectId());
        eventHook.setEventType(request.getEventType());

        return this.eventHookRepository.save(eventHook);
    }

    public EventHook delete(ModifyEventHookRequest request) {
        if (!this.accessGrantService.hasAdminAccess(request.getObjectId())) {
            throw new UnauthorizedException();
        }

        Optional<EventHook> eventHook
                = this.eventHookRepository.findById(request.getEventHookId());

        if (eventHook.isEmpty()) {
            throw new BadRequestException();
        }

        this.eventHookRepository.delete(eventHook.get());

        return eventHook.get();
    }

    public List<EventHook> getEventsForObject(String objectId) {
        if (!this.accessGrantService.hasAdminAccess(objectId)) {
            throw new UnauthorizedException();
        }

        return this.eventHookRepository.findByParentObjectId(objectId);
    }

    private void triggerEvent(Document document, EventHook eventHook) {
        logger.log(Level.INFO, "Triggering the \"{0}\" event for {1}",
                new Object[]{eventHook.getEventName(), document.getObjectId()});

        SessionToken sessionToken = this.sessionTokenService.create(
                eventHook.getClientKey());

        String requestUrl = String.format("%s?sessionToken=%s&documentId=%s",
                eventHook.getHandlerUrl(), sessionToken.getSessionToken(),
                document.getObjectId());
        HttpGet getRequest = new HttpGet(requestUrl);

        try {
            try (CloseableHttpResponse response
                    = this.httpClient.execute(getRequest)) {
                logger.log(Level.WARNING,
                        "Executed event \"{0}\" ({1}) with status code {2}.",
                        new Object[]{
                            eventHook.getEventName(), eventHook.getEventHookId(),
                            response.getStatusLine().getStatusCode()
                        });
            }
        } catch (IOException e) {
            String message = String.format("Could not fully execute event \"%s\" (%d).",
                    eventHook.getEventName(), eventHook.getEventHookId());
            logger.log(Level.WARNING, message, e);
        }
    }

    public void triggerCreatedEvents(Document document) {
        if (!this.accessGrantService.hasWriteAccess(document.getObjectId())) {
            throw new UnauthorizedException();
        }

        this.eventHookRepository.findByParentObjectIdAndEventType(
                document.getFolder().getObjectId(), EventType.CREATE)
                .forEach(eventHook -> {
                    this.triggerEvent(document, eventHook);
                });
    }

    public void triggerWriteEvents(Document document) {
        if (!this.accessGrantService.hasWriteAccess(document.getObjectId())) {
            throw new UnauthorizedException();
        }

        this.eventHookRepository.findByParentObjectIdAndEventType(
                document.getFolder().getObjectId(), EventType.WRITE)
                .forEach(eventHook -> {
                    this.triggerEvent(document, eventHook);
                });
    }

    public void triggerReadEvents(Document document) {
        if (!this.accessGrantService.hasReadAccess(document.getObjectId())) {
            throw new UnauthorizedException();
        }

        this.eventHookRepository.findByParentObjectIdAndEventType(
                document.getFolder().getObjectId(), EventType.READ)
                .forEach(eventHook -> {
                    this.triggerEvent(document, eventHook);
                });
    }

    public void triggerDeleteEvents(Document document) {
        if (!this.accessGrantService.hasReadAccess(document.getObjectId())) {
            throw new UnauthorizedException();
        }

        this.eventHookRepository.findByParentObjectIdAndEventType(
                document.getFolder().getObjectId(), EventType.DELETE)
                .forEach(eventHook -> {
                    this.triggerEvent(document, eventHook);
                });
    }
}
