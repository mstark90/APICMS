/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.services;

import com.starkindustriesne.apicms.ResourceNotFoundException;
import com.starkindustriesne.apicms.UnauthorizedException;
import com.starkindustriesne.apicms.domain.Folder;
import com.starkindustriesne.apicms.domain.FolderProperty;
import com.starkindustriesne.apicms.dto.CreateFolderRequest;
import com.starkindustriesne.apicms.dto.FolderResponse;
import com.starkindustriesne.apicms.repositories.FolderPropertyRepository;
import com.starkindustriesne.apicms.repositories.FolderRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 *
 * @author michaelstark
 */
@Service
public class FolderService {

    private final FolderPropertyRepository folderPropertyRepo;

    private final FolderRepository folderRepo;

    private final AccessGrantService accessGrantService;
    
    private final DocumentService documentService;

    public FolderService(FolderPropertyRepository folderPropertyRepo,
            FolderRepository folderRepo,
            AccessGrantService accessGrantService,
            DocumentService documentService) {
        this.folderPropertyRepo = folderPropertyRepo;
        this.folderRepo = folderRepo;
        this.accessGrantService = accessGrantService;
        this.documentService = documentService;
    }
    
    private boolean hasReadAccess(Folder folder) {
        return this.accessGrantService.hasReadAccess(folder.getObjectId());
    }

    @Transactional
    public FolderResponse modify(CreateFolderRequest request) {
        Folder folder = this.folderRepo.findByObjectId(request.getFolderId());

        if (folder == null) {
            folder = new Folder();

            folder.setCreated(new Date());
        }

        if (!this.accessGrantService.hasWriteAccess(null)) {
            throw new UnauthorizedException();
        }

        folder.setName(request.getName());
        folder.setLastModified(new Date());

        if (folder.getFolderId() > 0) {
            this.folderPropertyRepo.findByFolderId(folder.getFolderId())
                    .forEach(documentProperty -> {
                        this.folderPropertyRepo.delete(documentProperty);
                    });
        }

        boolean creating = folder.getFolderId() == 0;

        folder = this.folderRepo.save(folder);

        this.loadProperties(folder, request.getProperties());

        if (creating) {
            this.accessGrantService.createGrants(folder.getObjectId(), null);
        }

        return this.createFolderResponse(folder);
    }
    
    @Transactional
    public FolderResponse delete(String folderId) {
        Folder folder = this.folderRepo.findByObjectId(folderId);
        if(folder == null) {
            throw new ResourceNotFoundException();
        }
        
        if (!this.accessGrantService.hasWriteAccess(folder.getObjectId())) {
            throw new UnauthorizedException();
        }
        
        this.documentService.getByFolder(folder.getObjectId()).forEach(doc -> {
            this.documentService.delete(doc.getDocument().getObjectId());
        });
        
        FolderResponse response = this.createFolderResponse(folder);
        
        folderRepo.delete(folder);
        
        return response;
    }

    @Transactional
    public FolderResponse modify(String parentFolderId,
            CreateFolderRequest request) {
        Folder parentFolder = this.folderRepo.findByObjectId(parentFolderId);

        if (parentFolder == null) {
            throw new ResourceNotFoundException();
        }

        if (!this.accessGrantService.hasWriteAccess(parentFolderId)) {
            throw new UnauthorizedException();
        }

        Folder folder = this.folderRepo.findByObjectId(request.getFolderId());

        if (folder == null) {
            folder = new Folder();

            folder.setCreated(new Date());
            folder.setParent(parentFolder);
        }

        folder.setName(request.getName());
        folder.setLastModified(new Date());

        if (folder.getFolderId() > 0) {
            this.folderPropertyRepo.findByFolderId(folder.getFolderId())
                    .forEach(documentProperty -> {
                        this.folderPropertyRepo.delete(documentProperty);
                    });
        }

        boolean creating = folder.getFolderId() == 0;

        folder = this.folderRepo.save(folder);

        this.loadProperties(folder, request.getProperties());

        if (creating) {
            this.accessGrantService.createGrants(folder.getObjectId(),
                    parentFolderId);
        }

        return this.createFolderResponse(folder);
    }

    public List<FolderResponse> getAll() {
        return this.getAll(true);
    }
    
    public List<FolderResponse> getAll(boolean validateAccess) {
        return this.folderRepo.findAll().stream()
                .filter(folder -> validateAccess ? this.hasReadAccess(folder) : true)
                .map(this::createFolderResponse)
                .collect(Collectors.toList());
    }
    
    public List<FolderResponse> getByFolderIds(List<String> folderIds) {
        return this.folderRepo.findByObjectIdIn(folderIds).stream()
                .filter(this::hasReadAccess)
                .map(this::createFolderResponse)
                .collect(Collectors.toList());
    }

    public List<FolderResponse> getChildFolders(String parentFolderId) {
        Folder folder = this.folderRepo.findByObjectId(parentFolderId);

        if (folder == null) {
            throw new ResourceNotFoundException();
        }

        if (!this.accessGrantService.hasReadAccess(parentFolderId)) {
            throw new UnauthorizedException();
        }

        return this.folderRepo.findByParent(folder).stream()
                .filter(this::hasReadAccess)
                .map(this::createFolderResponse)
                .collect(Collectors.toList());
    }

    public FolderResponse getFolder(String objectId) {
        if(!this.accessGrantService.hasReadAccess(objectId)) {
            throw new UnauthorizedException();
        }
        
        return this.createFolderResponse(this.folderRepo
                .findByObjectId(objectId));
    }

    private Iterable<FolderProperty> loadProperties(Folder folder, Map<String, String> props) {
        if(props == null || props.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<FolderProperty> properties = new ArrayList<>();

        props.forEach((key, value) -> {
            FolderProperty prop = new FolderProperty();

            prop.setFolderId(folder.getFolderId());
            prop.setPropertyName(key);
            prop.setPropertyValue(value);

            properties.add(prop);
        });

        return this.folderPropertyRepo.saveAll(properties);
    }

    private FolderResponse createFolderResponse(Folder folder) {
        if (folder == null) {
            throw new ResourceNotFoundException();
        }

        FolderResponse response = new FolderResponse();
        Map<String, String> propertiesMap = new HashMap<>();

        this.folderPropertyRepo.findByFolderId(folder.getFolderId())
                .forEach(folderProperty -> {
                    propertiesMap.put(folderProperty.getPropertyName(), folderProperty.getPropertyValue());
                });

        response.setFolder(folder);
        response.setProperties(propertiesMap);
        return response;
    }
}
