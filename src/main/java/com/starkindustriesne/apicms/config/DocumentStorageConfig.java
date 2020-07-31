/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author michaelstark
 */
@Configuration
public class DocumentStorageConfig {
    @Value("${storage.baseFolder}")
    private String baseFolder;
    
    @Bean
    public String baseFolder() {
        return this.baseFolder;
    }
}
