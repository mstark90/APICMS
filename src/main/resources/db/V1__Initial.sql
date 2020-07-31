/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  michaelstark
 * Created: Jul 18, 2020
 */

CREATE DATABASE apicms;

USE apicms;

CREATE TABLE folders (
    folder_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    object_id VARCHAR(60) NOT NULL,
    name VARCHAR(100) NOT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT NOW(),
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    INDEX IDX_folder_object_id(object_id)
);

CREATE TABLE folder_tree (
    parent_folder_id BIGINT NOT NULL,
    child_folder_id BIGINT NOT NULL,
    FOREIGN KEY (parent_folder_id) REFERENCES folders (folder_id) ON DELETE CASCADE,
    FOREIGN KEY (child_folder_id) REFERENCES folders (folder_id) ON DELETE CASCADE
);

CREATE TABLE folder_properties (
    folder_property_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    folder_id BIGINT NOT NULL,
    property_name VARCHAR(75) NOT NULL,
    property_value VARCHAR(2048),
    FOREIGN KEY (folder_id) REFERENCES folders (folder_id) ON DELETE CASCADE
);

CREATE TABLE documents (
    document_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    folder_id BIGINT NOT NULL,
    object_id VARCHAR(60) NOT NULL,
    name VARCHAR(100) NOT NULL,
    storage_url VARCHAR(2048) NOT NULL DEFAULT '',
    last_modified TIMESTAMP NOT NULL DEFAULT NOW(),
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (folder_id) REFERENCES folders (folder_id) ON DELETE CASCADE,
    INDEX IDX_document_object_id(object_id)
);

CREATE TABLE document_properties (
    document_property_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    property_name VARCHAR(75) NOT NULL,
    property_value VARCHAR(2048),
    FOREIGN KEY (document_id) REFERENCES documents (document_id) ON DELETE CASCADE
);

CREATE TABLE access_keys (
    access_key_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    creator VARCHAR(60) NOT NULL,
    client_key VARCHAR(60) NOT NULL,
    client_secret VARCHAR(60) NOT NULL,
    key_name VARCHAR(100) NOT NULL,
    is_admin BIT NOT NULL DEFAULT 0,
    last_modified TIMESTAMP NOT NULL DEFAULT NOW(),
    created TIMESTAMP NOT NULL DEFAULT NOW()
    INDEX IDX_accesskeys_client_key(client_key)
);

CREATE TABLE access_grants (
    access_grant_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    object_id VARCHAR(60) NOT NULL,
    client_key VARCHAR(60) NOT NULL,
    access_grant_type INT NOT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT NOW(),
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    INDEX IDX_accessgrants_client_key(client_key)
);

CREATE TABLE event_hooks (
    event_hook_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    creator VARCHAR(60) NOT NULL,
    parent_object_id VARCHAR(60) NOT NULL,
    client_key VARCHAR(60) NOT NULL,
    handler_url VARCHAR(2048) NOT NULL,
    event_type INT NOT NULL DEFAULT 1,
    event_name VARCHAR(150) NOT NULL DEFAULT '',
    last_modified TIMESTAMP NOT NULL DEFAULT NOW(),
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    INDEX IDX_eventhooks_client_key(client_key),
    INDEX IDX_eventhooks_parent_object_id(parent_object_id)
);

CREATE TABLE session_tokens (
    session_token_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    session_token VARCHAR(60) NOT NULL,
    client_key VARCHAR(60) NOT NULL,
    expires_after TIMESTAMP NOT NULL DEFAULT TIMESTAMPADD(MINUTE, 5, NOW()),
    last_modified TIMESTAMP NOT NULL DEFAULT NOW(),
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    INDEX IDX_sessiontokens_session_token(session_token)
);