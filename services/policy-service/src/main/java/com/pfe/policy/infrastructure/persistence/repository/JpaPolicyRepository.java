package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.domain.model.PolicyStatus;
import com.pfe.policy.infrastructure.persistence.entity.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPolicyRepository extends JpaRepository<PolicyEntity, String> {
    Optional<PolicyEntity> findByPolicyNumber(String policyNumber);

    List<PolicyEntity> findByClientId(String clientId);

    List<PolicyEntity> findByStatus(PolicyStatus status);

    boolean existsByPolicyNumber(String policyNumber);
}
