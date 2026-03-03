package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.BeneficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaBeneficiaryRepository extends JpaRepository<BeneficiaryEntity, UUID> {
    List<BeneficiaryEntity> findByClientId(UUID clientId);
}
