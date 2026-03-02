package com.pfe.policy.domain.repository;

import com.pfe.policy.domain.model.Policy;
import com.pfe.policy.domain.model.PolicyStatus;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository {
    Policy save(Policy policy);

    Optional<Policy> findById(String id);

    Optional<Policy> findByPolicyNumber(String policyNumber);

    List<Policy> findByClientId(String clientId);

    List<Policy> findByStatus(PolicyStatus status);

    List<Policy> findAll();

    void deleteById(String id);

    boolean existsByPolicyNumber(String policyNumber);
}
