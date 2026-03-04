package com.pfe.claims.domain.repository;

import com.pfe.claims.domain.model.Payout;

import java.util.Optional;
import java.util.UUID;

public interface PayoutRepository {
    Payout save(Payout payout);

    Optional<Payout> findById(UUID id);

    Optional<Payout> findByClaimId(UUID claimId);

    void deleteById(UUID id);
}
