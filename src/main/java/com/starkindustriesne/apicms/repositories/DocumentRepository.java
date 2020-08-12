/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.repositories;

import com.starkindustriesne.apicms.domain.Document;
import com.starkindustriesne.apicms.domain.Folder;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author michaelstark
 */
@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {
    @Override
    List<Document> findAll();
    
    Document findByObjectId(String objectId);
    
    List<Document> findByObjectIdIn(List<String> objectIds);
    
    List<Document> findByFolder(Folder folder);
}
