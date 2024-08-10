package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author $ {Vladyslav Marii}
 **/

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for store data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */

public class DocumentManager {

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */

    //in Memory collection for storing data
    private final Map<String, Document> documents = new HashMap<>();
    private static int idCounter = 1; //unique id

    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document = Document.builder()
                    .id(String.valueOf(idCounter++))
                    .title(document.getTitle())
                    .content(document.getContent())
                    .author(document.getAuthor())
                    .created(document.getCreated())
                    .build();
        }
        documents.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */

    public List<Document> search(SearchRequest request) {
        return documents.values().stream()
                .filter(doc -> matchesTitlePrefix(doc, request.getTitlePrefixes()))
                .filter(doc -> containsContent(doc, request.getContainsContents()))
                .filter(doc -> matchesAuthorId(doc, request.getAuthorIds()))
                .filter(doc -> isCreatedInRange(doc, request.getCreatedFrom(), request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    private boolean matchesTitlePrefix(Document document, List<String> titlePrefixes) {
        if (titlePrefixes == null || titlePrefixes.isEmpty()) {
            return true;
        }
        return titlePrefixes.stream().anyMatch(prefix -> document.getTitle() != null && document.getTitle().startsWith(prefix));
    }

    private boolean containsContent(Document document, List<String> containsContents) {
        if (containsContents == null || containsContents.isEmpty()) {
            return true;
        }
        return containsContents.stream().anyMatch(content -> document.getContent() != null && document.getContent().contains(content));
    }

    private boolean matchesAuthorId(Document document, List<String> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return true;
        }
        return authorIds.stream().anyMatch(id -> document.getAuthor() != null && id.equals(document.getAuthor().getId()));
    }

    private boolean isCreatedInRange(Document document, Instant createdFrom, Instant createdTo) {
        if (createdFrom == null && createdTo == null) {
            return true;
        }
        Instant created = document.getCreated();
        if (created == null) {
            return false;
        }
        boolean afterFrom = createdFrom == null || !created.isBefore(createdFrom);
        boolean beforeTo = createdTo == null || !created.isAfter(createdTo);
        return afterFrom && beforeTo;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */

    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}