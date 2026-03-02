package com.pfe.policy.domain.repository;

import com.pfe.policy.domain.model.Beneficiary;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository {
    Beneficiary save(Beneficiary beneficiary);

    Optional<Beneficiary> findById(String id);

    List<Beneficiary> findByPolicyId(String policyId);

    void deleteById(String id);
}
