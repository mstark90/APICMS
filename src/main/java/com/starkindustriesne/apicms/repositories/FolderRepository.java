/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.repositories;

import com.starkindustriesne.apicms.domain.Folder;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author michaelstark
 */
@Repository
public interface FolderRepository extends CrudRepository<Folder, Long> {
    @Override
    List<Folder> findAll();
    
    Folder findByName(String name);
    
    Folder findByObjectId(String objectId);
    
    List<Folder> findByObjectIdIn(List<String> objectIds);
    
    List<Folder> findByParent(Folder parent);
}
