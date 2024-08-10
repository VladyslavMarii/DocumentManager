package org.example;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author $ {Vladyslav Marii}
 **/

public class DocumentManagerTest {

    public static void main(String[] args) {
        // Create an instance of DocumentManager
        DocumentManager manager = new DocumentManager();

        // Create authors
        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .id("author2")
                .name("Jane Smith")
                .build();

        // Create documents
        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .id("doc1")
                .title("Introduction to Java")
                .content("This document explains the basics of Java.")
                .author(author1)
                .created(Instant.now())
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .id("doc1")
                .title("Advanced Java Programming")
                .content("This document covers advanced topics in Java.")
                .author(author1)
                .created(Instant.now().minusSeconds(3600))
                .build();

        DocumentManager.Document doc3 = DocumentManager.Document.builder()
                .title("Kotlin for Java Developers")
                .content("A guide to Kotlin for Java developers.")
                .author(author2)
                .created(Instant.now().minusSeconds(86400))//one day
                .build();

        // Save documents
        DocumentManager.Document savedDoc1 = manager.save(doc1);
        DocumentManager.Document savedDoc2 = manager.save(doc2);
        DocumentManager.Document savedDoc3 = manager.save(doc3);

        // Retrieve and print document by ID
        Optional<DocumentManager.Document> retrievedDoc1 = manager.findById(savedDoc1.getId());
        System.out.println("Retrieved Document 1: " + retrievedDoc1);

        // Search for documents
        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Advanced"))
                .containsContents(List.of("Java"))
                .authorIds(List.of("author1"))
                .createdFrom(Instant.now().minusSeconds(86400*2))//two days
                .build();

        List<DocumentManager.Document> searchResults = manager.search(searchRequest);
        System.out.println("Search Results: " + searchResults);

        // Test with missing ID
        Optional<DocumentManager.Document> missingDoc = manager.findById("nonexistentId");
        System.out.println("Missing Document: " + missingDoc);

        // Test search with no filters
        DocumentManager.SearchRequest noFilterRequest = DocumentManager.SearchRequest.builder().build();
        List<DocumentManager.Document> allDocs = manager.search(noFilterRequest);
        System.out.println("All Documents: " + allDocs);

    }
}
