package com.pfe.claims.domain.repository;

import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimRepository {
    Claim save(Claim claim);

    Optional<Claim> findById(UUID id);

    List<Claim> findAll();

    List<Claim> findByClientId(UUID clientId);

    List<Claim> findByPolicyId(UUID policyId);

    void deleteById(UUID id);

    boolean existsByClaimNumber(String claimNumber);

    /**
     * Finds all claims with one of the given statuses that were created before a
     * deadline.
     * Used by the SLA scheduler to detect 48h SLA breaches.
     *
     * @param statuses list of open statuses to check
     * @param deadline cutoff time — claims created before this are overdue
     * @return list of overdue claims
     */
    List<Claim> findByStatusInAndCreatedAtBefore(List<ClaimStatus> statuses, LocalDateTime deadline);
}
