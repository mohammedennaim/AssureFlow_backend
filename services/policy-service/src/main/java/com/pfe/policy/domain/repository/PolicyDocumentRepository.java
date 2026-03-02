package com.pfe.policy.domain.repository;

import com.pfe.policy.domain.model.PolicyDocument;

import java.util.List;
import java.util.Optional;

public interface PolicyDocumentRepository {
    PolicyDocument save(PolicyDocument document);

    Optional<PolicyDocument> findById(String id);

    List<PolicyDocument> findByPolicyId(String policyId);

    void deleteById(String id);
}
