package com.pfe.policy.domain.repository;

import com.pfe.policy.domain.model.Coverage;

import java.util.List;
import java.util.Optional;

public interface CoverageRepository {
    Coverage save(Coverage coverage);

    Optional<Coverage> findById(String id);

    List<Coverage> findByPolicyId(String policyId);

    void deleteById(String id);
}
