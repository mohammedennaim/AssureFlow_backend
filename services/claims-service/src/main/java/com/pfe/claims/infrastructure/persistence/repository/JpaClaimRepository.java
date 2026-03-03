package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaClaimRepository extends JpaRepository<ClaimEntity, UUID> {
    List<ClaimEntity> findByClientId(UUID clientId);

    List<ClaimEntity> findByPolicyId(UUID policyId);

    boolean existsByClaimNumber(String claimNumber);
}
