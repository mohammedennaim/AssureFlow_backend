package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository {
    Document save(Document document);

    Optional<Document> findById(UUID id);

    List<Document> findByClientId(UUID clientId);

    void deleteById(UUID id);
}
