package com.pfe.claims.domain.repository;

import com.pfe.claims.domain.model.ClaimAssessment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimAssessmentRepository {
    ClaimAssessment save(ClaimAssessment assessment);

    Optional<ClaimAssessment> findById(UUID id);

    List<ClaimAssessment> findByClaimId(UUID claimId);

    void deleteById(UUID id);
}
