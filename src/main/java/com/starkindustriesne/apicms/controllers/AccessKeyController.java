/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.controllers;

import com.starkindustriesne.apicms.ResourceNotFoundException;
import com.starkindustriesne.apicms.domain.AccessGrant;
import com.starkindustriesne.apicms.domain.AccessKey;
import com.starkindustriesne.apicms.dto.AccessGrantResponse;
import com.starkindustriesne.apicms.dto.CreateAccessKeyRequest;
import com.starkindustriesne.apicms.dto.SecureValueResponse;
import com.starkindustriesne.apicms.services.AccessGrantService;
import com.starkindustriesne.apicms.services.AccessKeyService;
import com.starkindustriesne.apicms.services.DocumentService;
import com.starkindustriesne.apicms.services.FolderService;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/access-keys")
public class AccessKeyController {

    private final AccessKeyService accessKeyService;

    private final AccessGrantService accessGrantService;
    
    private final DocumentService documentService;
    
    private final FolderService folderService;

    public AccessKeyController(AccessKeyService accessKeyService,
            AccessGrantService accessGrantService,
            DocumentService documentService,
            FolderService folderService) {
        this.accessKeyService = accessKeyService;
        this.accessGrantService = accessGrantService;
        
        this.documentService = documentService;
        this.folderService = folderService;
    }

    @GetMapping
    public List<AccessKey> getAll() {
        return this.accessKeyService.getKeys();
    }
    
    @PostMapping
    public AccessKey create(@RequestBody CreateAccessKeyRequest request) {
        return this.accessKeyService.create(request);
    }

    @GetMapping("/{clientKey}/secret")
    public SecureValueResponse getSecret(@PathVariable String clientKey) {
        AccessKey key = this.accessKeyService.getByClientKey(clientKey);

        if (key == null) {
            throw new ResourceNotFoundException();
        }

        SecureValueResponse response = new SecureValueResponse();

        response.setValue(key.getClientSecret());

        return response;
    }

    @GetMapping("/{clientKey}/grants")
    public List<AccessGrantResponse> getGrants(@PathVariable String clientKey) {
        AccessKey key = this.accessKeyService.getByClientKey(clientKey);

        if (key == null) {
            throw new ResourceNotFoundException();
        }

        return this.accessGrantService.getGrantsForClientKey(clientKey).stream()
                .map((AccessGrant grant) -> {
                    AccessGrantResponse response = new AccessGrantResponse();
                    
                    response.setClientKey(grant.getClientKey());
                    response.setDocument(this.documentService
                            .getByDocumentId(grant.getObjectId()));
                    response.setFolder(this.folderService
                            .getFolder(grant.getObjectId()));
                    response.setAccessGrantType(grant.getAccessGrantType());
                    
                    return response;
                }).collect(Collectors.toList());
    }
}
