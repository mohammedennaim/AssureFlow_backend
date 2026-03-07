package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.domain.model.ClaimStatus;
import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaClaimRepository extends JpaRepository<ClaimEntity, UUID> {
    List<ClaimEntity> findByClientId(UUID clientId);

    List<ClaimEntity> findByPolicyId(UUID policyId);

    boolean existsByClaimNumber(String claimNumber);

    /**
     * Finds claim entities whose status is in the given set and createdAt is before
     * the deadline.
     * Used by the SLA scheduler to detect 48h SLA breaches.
     */
    List<ClaimEntity> findByStatusInAndCreatedAtBefore(List<ClaimStatus> statuses, LocalDateTime deadline);
}
