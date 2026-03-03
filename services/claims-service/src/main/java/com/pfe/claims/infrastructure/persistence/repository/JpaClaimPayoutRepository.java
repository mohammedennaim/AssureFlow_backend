package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.infrastructure.persistence.entity.ClaimPayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaClaimPayoutRepository extends JpaRepository<ClaimPayoutEntity, UUID> {
    Optional<ClaimPayoutEntity> findByClaimId(UUID claimId);
}
