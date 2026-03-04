package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.infrastructure.persistence.entity.PayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPayoutRepository extends JpaRepository<PayoutEntity, UUID> {
    Optional<PayoutEntity> findByClaimId(UUID claimId);
}
