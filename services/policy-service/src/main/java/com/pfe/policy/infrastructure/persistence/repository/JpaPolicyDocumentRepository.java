package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.infrastructure.persistence.entity.PolicyDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaPolicyDocumentRepository extends JpaRepository<PolicyDocumentEntity, String> {
    List<PolicyDocumentEntity> findByPolicyId(String policyId);
}
