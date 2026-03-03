package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaDocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    List<DocumentEntity> findByClientId(UUID clientId);
}
