package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.ClientHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaClientHistoryRepository extends JpaRepository<ClientHistoryEntity, UUID> {
    List<ClientHistoryEntity> findByClientIdOrderByPerformedAtDesc(UUID clientId);
}
