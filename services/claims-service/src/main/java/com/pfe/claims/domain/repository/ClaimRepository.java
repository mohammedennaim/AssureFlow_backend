package com.pfe.claims.domain.repository;

import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimStatus;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository {
    Claim save(Claim claim);

    Optional<Claim> findById(String id);

    Optional<Claim> findByClaimNumber(String claimNumber);

    List<Claim> findByClientId(String clientId);

    List<Claim> findByPolicyId(String policyId);

    List<Claim> findByStatus(ClaimStatus status);

    List<Claim> findAll();

    void deleteById(String id);

    boolean existsByClaimNumber(String claimNumber);
}
