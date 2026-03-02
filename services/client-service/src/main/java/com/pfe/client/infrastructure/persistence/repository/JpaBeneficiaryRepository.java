package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.BeneficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaBeneficiaryRepository extends JpaRepository<BeneficiaryEntity, String> {
    List<BeneficiaryEntity> findByClientId(String clientId);
}
