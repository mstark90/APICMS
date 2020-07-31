/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.controllers;

import com.starkindustriesne.apicms.domain.AccessGrant;
import com.starkindustriesne.apicms.dto.CreateAccessGrantRequest;
import com.starkindustriesne.apicms.dto.DocumentResponse;
import com.starkindustriesne.apicms.services.AccessGrantService;
import com.starkindustriesne.apicms.services.DocumentService;
import com.starkindustriesne.apicms.util.RequestUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author michaelstark
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {
    private final DocumentService documentService;
    
    private final AccessGrantService accessGrantService;
    
    public DocumentController(DocumentService documentService,
                              AccessGrantService accessGrantService) {
        this.documentService = documentService;
        this.accessGrantService = accessGrantService;
    }
    
    @GetMapping("/{documentId}")
    public DocumentResponse get(@PathVariable String documentId) {
        return this.documentService.getByDocumentId(documentId);
    }
    
    @DeleteMapping("/{documentId}")
    public DocumentResponse delete(@PathVariable String documentId) {
        return this.documentService.delete(documentId);
    }
    
    @GetMapping("/{documentId}/content")
    public void getContent(@PathVariable String documentId,
            HttpServletResponse response) throws IOException {
        DocumentResponse document = this.documentService
                .getByDocumentId(documentId);
        
        if(document.getProperties().containsKey("type")) {
            response.setContentType(document.getProperties().get("type"));
        } else {
            response.setContentType("application/octet-stream");
        }
        
        try(InputStream is = this.documentService.getContent(documentId);
                OutputStream os = response.getOutputStream()) {
            is.transferTo(os);
        }
    }
    
    @GetMapping("/{documentId}/grants")
    public List<AccessGrant> getAccessGrants(@PathVariable String documentId)
            throws IOException {
        DocumentResponse document = this.documentService
                .getByDocumentId(documentId);
        
        return this.accessGrantService.getObjectAccessGrants(document
                .getDocument().getObjectId());
    }
    
    @PostMapping("/{documentId}/grants")
    public AccessGrant createAccessGrant(@PathVariable String documentId,
            @RequestBody CreateAccessGrantRequest request) {
        DocumentResponse document = this.documentService
                .getByDocumentId(documentId);
        
        return this.accessGrantService.create(RequestUtil.getClientKey(),
                request.getClientKey(), document.getDocument().getObjectId(),
                request.getAccessGrantType());
    }
    
    @PostMapping("/{documentId}/content")
    public DocumentResponse uploadContent(@PathVariable String documentId,
            @RequestParam("content") MultipartFile content) 
        throws IOException {
        return this.documentService
                .loadContent(documentId, content.getInputStream());
    }
}
