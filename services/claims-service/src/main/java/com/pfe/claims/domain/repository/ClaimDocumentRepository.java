package com.pfe.claims.domain.repository;

import com.pfe.claims.domain.model.ClaimDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimDocumentRepository {
    ClaimDocument save(ClaimDocument document);

    Optional<ClaimDocument> findById(UUID id);

    List<ClaimDocument> findByClaimId(UUID claimId);

    void deleteById(UUID id);
}
