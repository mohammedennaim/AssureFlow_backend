package com.pfe.policy.domain.repository;

import com.pfe.policy.domain.model.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository {
    Policy save(Policy policy);

    Optional<Policy> findById(String id);

    List<Policy> findAll();

    Page<Policy> findAll(Pageable pageable);

    List<Policy> findByClientId(String clientId);

    void deleteById(String id);

    boolean existsByPolicyNumber(String policyNumber);
}
