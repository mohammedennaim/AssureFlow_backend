package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.ClientHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaClientHistoryRepository extends JpaRepository<ClientHistoryEntity, String> {
    List<ClientHistoryEntity> findByClientIdOrderByPerformedAtDesc(String clientId);
}
