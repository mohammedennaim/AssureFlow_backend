package com.pfe.client.application.service;

import com.pfe.client.application.dto.BeneficiaryRequest;
import com.pfe.client.application.dto.BeneficiaryResponse;

import java.util.List;
import java.util.UUID;

public interface BeneficiaryService {
    BeneficiaryResponse createBeneficiary(UUID clientId, BeneficiaryRequest request);

    BeneficiaryResponse getBeneficiaryById(UUID id);

    List<BeneficiaryResponse> getBeneficiariesByClientId(UUID clientId);

    void deleteBeneficiary(UUID id);
}
