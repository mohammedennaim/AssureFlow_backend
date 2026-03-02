package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.infrastructure.persistence.entity.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaPolicyRepository extends JpaRepository<PolicyEntity, String> {
    List<PolicyEntity> findByClientId(String clientId);

    boolean existsByPolicyNumber(String policyNumber);
}
