package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.infrastructure.persistence.entity.ClaimAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaClaimAssessmentRepository extends JpaRepository<ClaimAssessmentEntity, UUID> {
    List<ClaimAssessmentEntity> findByClaimId(UUID claimId);
}
