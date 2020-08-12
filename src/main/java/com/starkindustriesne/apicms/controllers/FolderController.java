/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.controllers;

import com.starkindustriesne.apicms.domain.AccessGrant;
import com.starkindustriesne.apicms.domain.EventHook;
import com.starkindustriesne.apicms.dto.CreateAccessGrantRequest;
import com.starkindustriesne.apicms.dto.CreateDocumentRequest;
import com.starkindustriesne.apicms.dto.CreateFolderRequest;
import com.starkindustriesne.apicms.dto.DocumentResponse;
import com.starkindustriesne.apicms.dto.FolderResponse;
import com.starkindustriesne.apicms.dto.ModifyEventHookRequest;
import com.starkindustriesne.apicms.dto.SearchRequest;
import com.starkindustriesne.apicms.services.AccessGrantService;
import com.starkindustriesne.apicms.services.DocumentService;
import com.starkindustriesne.apicms.services.EventHookService;
import com.starkindustriesne.apicms.services.FolderService;
import com.starkindustriesne.apicms.services.SearchEngine;
import com.starkindustriesne.apicms.util.RequestUtil;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author michaelstark
 */
@RestController
@RequestMapping("/folders")
public class FolderController {
    private final FolderService folderService;
    private final DocumentService documentService;
    private final AccessGrantService accessGrantService;
    private final EventHookService eventHookService;
    private final SearchEngine searchEngine;
    
    public FolderController(FolderService folderService,
                            DocumentService documentService,
                            AccessGrantService accessGrantService,
                            EventHookService eventHookService,
                            SearchEngine searchEngine) {
        this.folderService = folderService;
        this.documentService = documentService;
        this.accessGrantService = accessGrantService;
        this.eventHookService = eventHookService;
        this.searchEngine = searchEngine;
    }
    
    @PostMapping
    public FolderResponse create(@RequestBody CreateFolderRequest request) {
        return this.folderService.modify(request);
    }
    
    @PostMapping("/query")
    public List<FolderResponse> query(@RequestBody SearchRequest request)
        throws IOException, ParseException {
        return this.searchEngine.queryFolders(request);
    }
    
    @GetMapping("/all")
    public List<FolderResponse> getAll() {
        return this.folderService.getAll();
    }
    
    @GetMapping("/{folderId}")
    public FolderResponse getFolder(@PathVariable String folderId) {
        return this.folderService.getFolder(folderId);
    }
    
    @DeleteMapping("/{folderId}")
    public FolderResponse delete(@PathVariable String folderId) {
        return this.folderService.delete(folderId);
    }
    
    @GetMapping("/{folderId}/events")
    public List<EventHook> getFolderEvents(@PathVariable String folderId) {
        FolderResponse folder = this.folderService.getFolder(folderId);
        return this.eventHookService.getEventsForObject(folderId);
    }
    
    @PostMapping("/{folderId}/events")
    public EventHook createFolderEvent(@PathVariable String folderId,
            @RequestBody ModifyEventHookRequest request) {
        FolderResponse folder = this.folderService.getFolder(folderId);
        
        request.setObjectId(folderId);
        
        return this.eventHookService.modify(request);
    }
    
    @GetMapping("/{folderId}/children")
    public List<FolderResponse> getChildrenFolders(@PathVariable String folderId) {
        return this.folderService.getChildFolders(folderId);
    }
    
    @PostMapping("/{folderId}/children")
    public FolderResponse createChildFolder(@PathVariable String folderId,
            @RequestBody CreateFolderRequest request) {
        return this.folderService.modify(folderId, request);
    }
    
    @GetMapping("/{folderId}/documents")
    public List<DocumentResponse> getFolderDocuments(@PathVariable String folderId) {
        return this.documentService.getByFolder(folderId);
    }
    
    @PostMapping("/{folderId}/documents")
    public DocumentResponse uploadDocumentToFolder(
            @PathVariable String folderId,
            @RequestBody CreateDocumentRequest request) {
        
        request.setFolderId(folderId);
        
        return this.documentService.modify(request);
    }
    
    @GetMapping("/{folderId}/grants")
    public List<AccessGrant> getAccessGrants(@PathVariable String folderId)
            throws IOException {
        FolderResponse folderResponse = this.folderService.getFolder(folderId);
        
        return this.accessGrantService.getObjectAccessGrants(folderResponse
                .getFolder().getObjectId());
    }
    
    @PostMapping("/{folderId}/grants")
    public AccessGrant createAccessGrant(@PathVariable String folderId,
            @RequestBody CreateAccessGrantRequest request) {
        FolderResponse folderResponse = this.folderService.getFolder(folderId);
        
        return this.accessGrantService.create(RequestUtil.getClientKey(),
                request.getClientKey(), folderResponse.getFolder().getObjectId(),
                request.getAccessGrantType());
    }
}
