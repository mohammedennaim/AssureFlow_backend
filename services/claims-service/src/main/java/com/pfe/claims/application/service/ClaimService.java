package com.pfe.claims.application.service;

import com.pfe.claims.application.dto.ClaimDto;
import com.pfe.claims.application.dto.CreateClaimRequest;
import com.pfe.claims.application.dto.UpdateClaimRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ClaimService {
    ClaimDto createClaim(CreateClaimRequest request);

    ClaimDto getClaimById(UUID id);

    List<ClaimDto> getClaimsByClientId(UUID clientId);

    List<ClaimDto> getClaimsByPolicyId(UUID policyId);

    List<ClaimDto> getAllClaims();

    ClaimDto updateClaim(UUID id, UpdateClaimRequest request);

    void submitClaim(UUID id);

    void reviewClaim(UUID id);

    void approveClaim(UUID id, BigDecimal approvedAmount, UUID approvedBy);

    void rejectClaim(UUID id, String reason);

    void requestInfo(UUID id);

    void markAsPaid(UUID id);

    void closeClaim(UUID id);

    void deleteClaim(UUID id);
}
