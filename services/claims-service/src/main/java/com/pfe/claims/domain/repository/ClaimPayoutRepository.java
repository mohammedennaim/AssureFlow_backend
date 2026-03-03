package com.pfe.claims.domain.repository;

import com.pfe.claims.domain.model.ClaimPayout;

import java.util.Optional;
import java.util.UUID;

public interface ClaimPayoutRepository {
    ClaimPayout save(ClaimPayout payout);

    Optional<ClaimPayout> findById(UUID id);

    Optional<ClaimPayout> findByClaimId(UUID claimId);

    void deleteById(UUID id);
}
