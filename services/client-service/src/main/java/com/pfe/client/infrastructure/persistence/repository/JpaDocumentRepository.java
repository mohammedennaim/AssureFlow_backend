package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaDocumentRepository extends JpaRepository<DocumentEntity, String> {
    List<DocumentEntity> findByClientId(String clientId);
}
