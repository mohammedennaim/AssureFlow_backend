package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.domain.model.ClaimStatus;
import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaClaimRepository extends JpaRepository<ClaimEntity, String> {
    Optional<ClaimEntity> findByClaimNumber(String claimNumber);

    List<ClaimEntity> findByClientId(String clientId);

    List<ClaimEntity> findByPolicyId(String policyId);

    List<ClaimEntity> findByStatus(ClaimStatus status);

    boolean existsByClaimNumber(String claimNumber);
}
