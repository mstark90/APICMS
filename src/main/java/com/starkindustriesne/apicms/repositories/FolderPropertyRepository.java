/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.repositories;

import com.starkindustriesne.apicms.domain.Folder;
import com.starkindustriesne.apicms.domain.FolderProperty;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author michaelstark
 */
@Repository
public interface FolderPropertyRepository extends CrudRepository<FolderProperty, Long> {
    List<FolderProperty> findByFolderId(long folderId);
}
