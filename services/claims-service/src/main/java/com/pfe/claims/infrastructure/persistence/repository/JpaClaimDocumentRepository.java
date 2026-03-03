package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.infrastructure.persistence.entity.ClaimDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaClaimDocumentRepository extends JpaRepository<ClaimDocumentEntity, UUID> {
    List<ClaimDocumentEntity> findByClaimId(UUID claimId);
}
