/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.apicms.services;

import com.starkindustriesne.apicms.dto.DocumentResponse;
import com.starkindustriesne.apicms.dto.FolderResponse;
import com.starkindustriesne.apicms.dto.SearchRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author michaelstark
 */
@Service
public class SearchEngine {

    private final DocumentService documentService;

    private final FolderService folderService;

    private final Directory folderIndexDirectory;
    private final Directory documentIndexDirectory;

    private final IndexWriter folderIndexWriter;
    private final IndexWriter documentIndexWriter;

    private DirectoryReader folderIndexReader;
    private DirectoryReader documentIndexReader;

    private IndexSearcher folderSearcher;
    private IndexSearcher documentSearcher;

    private final File documentIndexPath;
    private final File folderIndexPath;

    private final QueryParser queryParser;

    private static final Logger logger
            = Logger.getLogger(SearchEngine.class.getName());

    public SearchEngine(DocumentService documentService,
            FolderService folderService) throws IOException {
        this.documentService = documentService;

        this.folderService = folderService;

        this.documentIndexPath = File.createTempFile("apicms_", ".idx");
        this.folderIndexPath = File.createTempFile("apicms_", ".idx");

        this.documentIndexPath.delete();
        this.folderIndexPath.delete();

        this.documentIndexDirectory
                = new MMapDirectory(this.documentIndexPath.toPath());
        this.folderIndexDirectory
                = new MMapDirectory(this.folderIndexPath.toPath());

        IndexWriterConfig folderWriterConfig
                = new IndexWriterConfig(new StandardAnalyzer());

        IndexWriterConfig documentWriterConfig
                = new IndexWriterConfig(new StandardAnalyzer());

        this.folderIndexWriter = new IndexWriter(this.folderIndexDirectory,
                folderWriterConfig);
        this.documentIndexWriter = new IndexWriter(this.documentIndexDirectory,
                documentWriterConfig);

        this.queryParser = new QueryParser("name",
                folderWriterConfig.getAnalyzer());
    }

    @PostConstruct
    protected void loadIndex() throws IOException {
        logger.log(Level.INFO, "Indexing APICMS folders for searching.");

        this.folderService.getAll(false)
                .stream().map(this::createLuceneDoc)
                .forEach(doc -> {
                    try {
                        long seqNum = this.folderIndexWriter.addDocument(doc);
                        logger.log(Level.INFO,
                                "Indexed folder sequence number: {0}",
                                new Object[]{seqNum});
                    } catch (IOException e) {
                        logger.log(Level.WARNING,
                                "Could not index the specified folder: ", e);
                    }
                });

        this.folderIndexWriter.commit();

        logger.log(Level.INFO, "Indexing APICMS documents for searching.");

        this.documentService.getAll(false)
                .stream().map(this::createLuceneDoc)
                .forEach(doc -> {
                    try {
                        long seqNum = this.documentIndexWriter.addDocument(doc);

                        logger.log(Level.INFO,
                                "Indexed folder sequence number: {0}",
                                new Object[]{seqNum});
                    } catch (IOException e) {
                        logger.log(Level.WARNING,
                                "Could not index the specified document: ", e);
                    }
                });

        this.documentIndexWriter.commit();

        this.folderIndexReader
                = DirectoryReader.open(this.folderIndexDirectory);
        this.documentIndexReader
                = DirectoryReader.open(this.documentIndexDirectory);

        this.folderSearcher = new IndexSearcher(this.folderIndexReader);
        this.documentSearcher = new IndexSearcher(this.documentIndexReader);
    }

    private void checkIndexReaders() throws IOException {
        DirectoryReader newReader
                = DirectoryReader.openIfChanged(this.folderIndexReader);
        if (newReader != null) {
            this.folderIndexReader.close();
            this.folderIndexReader = newReader;
        }

        newReader = DirectoryReader.openIfChanged(this.documentIndexReader);
        if (newReader != null) {
            this.documentIndexReader.close();
            this.documentIndexReader = newReader;
        }

        this.folderSearcher = new IndexSearcher(this.folderIndexReader);
        this.documentSearcher = new IndexSearcher(this.documentIndexReader);
    }

    @PreDestroy
    protected void clearIndex() throws IOException {
        this.documentIndexWriter.close();
        this.folderIndexWriter.close();

        if (this.documentIndexReader != null) {
            this.documentIndexReader.close();
        }

        if (this.folderIndexReader != null) {
            this.folderIndexReader.close();
        }

        this.documentIndexPath.delete();
        this.folderIndexPath.delete();
    }

    private Document createLuceneDoc(DocumentResponse document) {
        Document indexDocument = new Document();

        com.starkindustriesne.apicms.domain.Document documentObj
                = document.getDocument();

        logger.log(Level.INFO, "Converting document {0} for indexing in Lucene.",
                new Object[]{documentObj.getName()});

        indexDocument.add(new StringField("objectId", documentObj.getObjectId(),
                Field.Store.YES));
        indexDocument.add(new TextField("name",
                documentObj.getName(), Field.Store.YES));
        indexDocument.add(new TextField("parentName",
                documentObj.getFolder().getName(), Field.Store.YES));
        indexDocument.add(new TextField("created",
                DateTools.dateToString(documentObj.getCreated(),
                        DateTools.Resolution.MILLISECOND), Field.Store.YES));
        indexDocument.add(new TextField("lastModified",
                DateTools.dateToString(documentObj.getLastModified(),
                        DateTools.Resolution.MILLISECOND), Field.Store.YES));

        document.getProperties().forEach((String key, String value) -> {
            indexDocument.add(new TextField(key, value, Field.Store.YES));
        });

        return indexDocument;
    }

    private Document createLuceneDoc(FolderResponse folder) {
        Document indexDocument = new Document();

        com.starkindustriesne.apicms.domain.Folder folderObj
                = folder.getFolder();

        com.starkindustriesne.apicms.domain.Folder parentObj
                = folderObj.getParent();

        logger.log(Level.INFO, "Converting folder {0} for indexing in Lucene.",
                new Object[]{folderObj.getName()});

        indexDocument.add(new StringField("objectId", folderObj.getObjectId(),
                Field.Store.YES));
        indexDocument.add(new TextField("name",
                folderObj.getName(), Field.Store.YES));
        indexDocument.add(new TextField("parentName",
                parentObj != null ? parentObj.getName() : "",
                Field.Store.YES));
        indexDocument.add(new TextField("created",
                DateTools.dateToString(folderObj.getCreated(),
                        DateTools.Resolution.MILLISECOND), Field.Store.YES));
        indexDocument.add(new TextField("lastModified",
                DateTools.dateToString(folderObj.getLastModified(),
                        DateTools.Resolution.MILLISECOND), Field.Store.YES));

        folder.getProperties().forEach((String key, String value) -> {
            indexDocument.add(new TextField(key, value, Field.Store.YES));
        });

        return indexDocument;
    }

    @Async
    public void indexDocument(DocumentResponse document) throws IOException {
        this.documentIndexWriter.addDocument(createLuceneDoc(document));
    }

    @Async
    public void indexFolder(FolderResponse folder) throws IOException {
        this.folderIndexWriter.addDocument(createLuceneDoc(folder));
    }

    public List<DocumentResponse> queryDocuments(SearchRequest query)
            throws ParseException, IOException {
        this.checkIndexReaders();

        Query q = this.queryParser.parse(query.getQuery());

        var docs = this.documentSearcher.search(q, 10);

        List<String> documentIds = new ArrayList<>();

        for (ScoreDoc result : docs.scoreDocs) {
            documentIds.add(this.documentIndexReader.document(result.doc)
                    .get("objectId"));
        }

        return this.documentService.getByDocumentIds(documentIds);
    }

    public List<FolderResponse> queryFolders(SearchRequest query)
            throws ParseException, IOException {
        this.checkIndexReaders();

        Query q = this.queryParser.parse(query.getQuery());

        var docs = this.folderSearcher.search(q, 10);

        List<String> folderIds = new ArrayList<>();

        for (ScoreDoc result : docs.scoreDocs) {
            folderIds.add(this.folderIndexReader.document(result.doc)
                    .get("objectId"));
        }

        return this.folderService.getByFolderIds(folderIds);
    }
}
