/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.services;

import com.starkindustriesne.apicms.APICMSException;
import com.starkindustriesne.apicms.ResourceNotFoundException;
import com.starkindustriesne.apicms.UnauthorizedException;
import com.starkindustriesne.apicms.domain.Document;
import com.starkindustriesne.apicms.domain.DocumentProperty;
import com.starkindustriesne.apicms.domain.Folder;
import com.starkindustriesne.apicms.dto.CreateDocumentRequest;
import com.starkindustriesne.apicms.dto.DocumentResponse;
import com.starkindustriesne.apicms.repositories.DocumentPropertyRepository;
import com.starkindustriesne.apicms.repositories.DocumentRepository;
import com.starkindustriesne.apicms.repositories.FolderRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 *
 * @author michaelstark
 */
@Service
public class DocumentService {

    private final DocumentRepository documentRepo;
    private final DocumentPropertyRepository documentPropertyRepo;

    private final FolderRepository folderRepo;

    private final AccessGrantService accessGrantService;
    
    private final EventHookService eventHookService;

    private final File baseFolder;

    private final Logger logger = Logger.getLogger(DocumentService.class.getName());

    public DocumentService(DocumentRepository documentRepo,
            DocumentPropertyRepository documentPropertyRepo,
            FolderRepository folderRepo,
            AccessGrantService accessGrantService,
            EventHookService eventHookService,
            String baseFolder) {
        this.documentRepo = documentRepo;
        this.documentPropertyRepo = documentPropertyRepo;
        this.folderRepo = folderRepo;
        
        this.accessGrantService = accessGrantService;
        this.eventHookService = eventHookService;
        
        this.baseFolder = new File(baseFolder);
    }

    public DocumentResponse getByDocumentId(String documentId) {
        Document document = this.documentRepo.findByObjectId(documentId);
        
        if(document != null) {
            this.eventHookService.triggerReadEvents(document);
        }

        return this.createDocumentResponse(document);
    }

    public List<DocumentResponse> getByFolder(String folderId) {
        Folder folder = this.folderRepo.findByObjectId(folderId);

        if (!this.accessGrantService.hasReadAccess(folderId)) {
            throw new UnauthorizedException();
        }

        List<Document> documents = this.documentRepo.findByFolder(folder);

        return documents.stream()
                .filter(document -> this.accessGrantService
                .hasReadAccess(document.getObjectId()))
                .map(document -> {
                    return this.createDocumentResponse(document);
                }).collect(Collectors.toList());
    }
    
    @Transactional
    public DocumentResponse delete(String documentId) {
        Document document = this.documentRepo.findByObjectId(documentId);
        if(document == null) {
            throw new ResourceNotFoundException();
        }
        
        Folder folder = document.getFolder();
        
        if (!this.accessGrantService.hasWriteAccess(folder.getObjectId())
                || !this.accessGrantService.hasWriteAccess(document.getObjectId())) {
            throw new UnauthorizedException();
        }
        
        DocumentResponse response = this.createDocumentResponse(document);
        
        documentRepo.delete(document);
        
        File file = new File(baseFolder, document.getObjectId());
        
        file.delete();
        
        this.eventHookService.triggerDeleteEvents(document);
        
        return response;
    }

    public DocumentResponse modify(CreateDocumentRequest request) {
        Document document = this.documentRepo.findByObjectId(request.getDocumentId());
        Folder folder = this.folderRepo.findByObjectId(request.getFolderId());

        if (!this.accessGrantService.hasWriteAccess(request.getFolderId())
                || (document != null && !this.accessGrantService
                        .hasWriteAccess(request.getDocumentId()))) {
            throw new UnauthorizedException();
        }

        if (document == null) {
            document = new Document();

            document.setCreated(new Date());
        }

        document.setFolder(folder);
        document.setName(request.getDocumentName());
        document.setLastModified(new Date());

        if (document.getDocumentId() > 0) {
            this.documentPropertyRepo.findByDocumentId(document.getDocumentId())
                    .forEach(documentProperty -> {
                        this.documentPropertyRepo.delete(documentProperty);
                    });
        }

        boolean creating = document.getDocumentId() == 0;

        document = documentRepo.save(document);

        if (creating) {
            this.accessGrantService.createGrants(document.getObjectId(),
                    document.getFolder().getObjectId());
            this.eventHookService.triggerCreatedEvents(document);
        } else {
            this.eventHookService.triggerWriteEvents(document);
        }

        Iterable<DocumentProperty> rawProps
                = this.loadProperties(document, request.getProperties());

        return this.createDocumentResponse(document);
    }

    public DocumentResponse loadContent(String documentId, InputStream content) {
        Document document = this.documentRepo.findByObjectId(documentId);
        
        if(document == null) {
            throw new ResourceNotFoundException();
        }

        if (!this.accessGrantService.hasWriteAccess(documentId)) {
            throw new UnauthorizedException();
        }

        File file = new File(baseFolder, document.getObjectId());

        try {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buf = new byte[4096];
                int read = 0;

                while ((read = content.read(buf)) > 0) {
                    fos.write(buf, 0, read);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not store the data: ", e);

            throw new APICMSException(e);
        }
        
        this.eventHookService.triggerWriteEvents(document);

        return this.createDocumentResponse(document);
    }

    public InputStream getContent(String documentId) throws IOException {
        Document document = this.documentRepo.findByObjectId(documentId);
        
        if(document == null) {
            throw new ResourceNotFoundException();
        }

        if (!this.accessGrantService
                .hasReadAccess(documentId)
                && !this.accessGrantService.hasReadAccess(document
                        .getFolder().getObjectId())) {
            throw new UnauthorizedException();
        }

        File file = new File(baseFolder, document.getObjectId());
        
        if(!file.exists()) {
            throw new ResourceNotFoundException();
        }
        
        this.eventHookService.triggerReadEvents(document);

        return new FileInputStream(file);
    }

    private Iterable<DocumentProperty> loadProperties(Document document, Map<String, String> props) {
        List<DocumentProperty> properties = new ArrayList<>();

        props.forEach((key, value) -> {
            DocumentProperty prop = new DocumentProperty();

            prop.setDocumentId(document.getDocumentId());
            prop.setPropertyName(key);
            prop.setPropertyValue(value);

            properties.add(prop);
        });

        return this.documentPropertyRepo.saveAll(properties);
    }

    private DocumentResponse createDocumentResponse(Document document) {
        if(document == null) {
            throw new ResourceNotFoundException();
        }
        
        DocumentResponse response = new DocumentResponse();
        
        Iterable<DocumentProperty> rawProps = this.documentPropertyRepo
                .findByDocumentId(document.getDocumentId());
        Map<String, String> propertiesMap = new HashMap<>();

        rawProps.forEach(documentProperty -> {
            propertiesMap.put(documentProperty.getPropertyName(), documentProperty.getPropertyValue());
        });

        response.setDocument(document);
        response.setProperties(propertiesMap);
        return response;
    }
}
