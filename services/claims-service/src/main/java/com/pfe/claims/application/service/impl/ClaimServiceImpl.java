package com.pfe.claims.application.service.impl;

import com.pfe.claims.application.dto.*;
import com.pfe.claims.application.service.ClaimService;
import com.pfe.claims.domain.exception.ClaimNotFoundException;
import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimDocument;
import com.pfe.claims.domain.model.ClaimStatus;
import com.pfe.claims.domain.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;

    @Override
    @Transactional
    public ClaimResponse submitClaim(ClaimRequest request) {
        log.info("Submitting claim for policy: {}", request.getPolicyId());
        String claimNumber = generateClaimNumber();
        while (claimRepository.existsByClaimNumber(claimNumber)) {
            claimNumber = generateClaimNumber();
        }

        Claim claim = Claim.builder()
                .id(UUID.randomUUID().toString())
                .claimNumber(claimNumber)
                .policyId(request.getPolicyId())
                .clientId(request.getClientId())
                .description(request.getDescription())
                .status(ClaimStatus.SUBMITTED)
                .incidentDate(request.getIncidentDate())
                .claimedAmount(request.getClaimedAmount())
                .build();

        return toResponse(claimRepository.save(claim));
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimById(String id) {
        return toResponse(claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByClientId(String clientId) {
        return claimRepository.findByClientId(clientId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByPolicyId(String policyId) {
        return claimRepository.findByPolicyId(policyId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByStatus(ClaimStatus status) {
        return claimRepository.findByStatus(status).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClaimResponse startReview(String id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.startReview();
        return toResponse(claimRepository.save(claim));
    }

    @Override
    @Transactional
    public ClaimResponse approveClaim(String id, ClaimApprovalRequest approvalRequest) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.approve(approvalRequest.getApprovedAmount());
        return toResponse(claimRepository.save(claim));
    }

    @Override
    @Transactional
    public ClaimResponse rejectClaim(String id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.reject();
        return toResponse(claimRepository.save(claim));
    }

    @Override
    @Transactional
    public void deleteClaim(String id) {
        if (claimRepository.findById(id).isEmpty()) {
            throw new ClaimNotFoundException(id);
        }
        claimRepository.deleteById(id);
    }

    private String generateClaimNumber() {
        return "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ClaimResponse toResponse(Claim claim) {
        List<ClaimDocumentResponse> documents = claim.getDocuments() == null ? List.of() :
                claim.getDocuments().stream().map(d -> ClaimDocumentResponse.builder()
                        .id(d.getId())
                        .claimId(d.getClaimId())
                        .documentName(d.getDocumentName())
                        .documentUrl(d.getDocumentUrl())
                        .uploadDate(d.getUploadDate())
                        .build()).collect(Collectors.toList());

        return ClaimResponse.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .policyId(claim.getPolicyId())
                .clientId(claim.getClientId())
                .description(claim.getDescription())
                .status(claim.getStatus() != null ? claim.getStatus().name() : null)
                .incidentDate(claim.getIncidentDate())
                .claimedAmount(claim.getClaimedAmount())
                .approvedAmount(claim.getApprovedAmount())
                .documents(documents)
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}
