package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.Beneficiary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeneficiaryRepository {
    Beneficiary save(Beneficiary beneficiary);

    Optional<Beneficiary> findById(UUID id);

    List<Beneficiary> findByClientId(UUID clientId);

    void deleteById(UUID id);

    void deleteByClientId(UUID clientId);
}
