package com.pfe.claims.application.service;

import com.pfe.claims.application.dto.ClaimApprovalRequest;
import com.pfe.claims.application.dto.ClaimRequest;
import com.pfe.claims.application.dto.ClaimResponse;
import com.pfe.claims.domain.model.ClaimStatus;

import java.util.List;

public interface ClaimService {
    ClaimResponse submitClaim(ClaimRequest request);

    ClaimResponse getClaimById(String id);

    List<ClaimResponse> getClaimsByClientId(String clientId);

    List<ClaimResponse> getClaimsByPolicyId(String policyId);

    List<ClaimResponse> getClaimsByStatus(ClaimStatus status);

    List<ClaimResponse> getAllClaims();

    ClaimResponse startReview(String id);

    ClaimResponse approveClaim(String id, ClaimApprovalRequest approvalRequest);

    ClaimResponse rejectClaim(String id);

    void deleteClaim(String id);
}
