package com.pfe.client.application.service;

import com.pfe.client.application.dto.BeneficiaryRequest;
import com.pfe.client.application.dto.BeneficiaryResponse;

import java.util.List;

public interface BeneficiaryService {
    BeneficiaryResponse createBeneficiary(String clientId, BeneficiaryRequest request);

    BeneficiaryResponse getBeneficiaryById(String id);

    List<BeneficiaryResponse> getBeneficiariesByClientId(String clientId);

    void deleteBeneficiary(String id);
}
