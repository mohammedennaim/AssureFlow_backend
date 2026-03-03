package com.pfe.claims.domain.repository;

import com.pfe.claims.domain.model.Claim;

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
}
