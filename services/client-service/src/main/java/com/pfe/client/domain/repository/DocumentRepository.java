package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.Document;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository {
    Document save(Document document);

    Optional<Document> findById(String id);

    List<Document> findByClientId(String clientId);

    void deleteById(String id);
}
