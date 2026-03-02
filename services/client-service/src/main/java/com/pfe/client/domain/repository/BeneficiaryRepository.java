package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.Beneficiary;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository {
    Beneficiary save(Beneficiary beneficiary);

    Optional<Beneficiary> findById(String id);

    List<Beneficiary> findByClientId(String clientId);

    void deleteById(String id);

    void deleteByClientId(String clientId);
}
