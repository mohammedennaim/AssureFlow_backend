package com.pfe.claims.application.service.impl;

import com.pfe.claims.application.dto.ClaimDto;
import com.pfe.claims.application.dto.CreateClaimRequest;
import com.pfe.claims.application.dto.UpdateClaimRequest;
import com.pfe.claims.application.mapper.ClaimMapper;
import com.pfe.claims.application.service.ClaimService;
import com.pfe.claims.domain.exception.ClaimNotFoundException;
import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimStatus;
import com.pfe.claims.domain.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;

    @Override
    @Transactional
    public ClaimDto createClaim(CreateClaimRequest request) {
        Claim claim = claimMapper.toDomain(request);
        claim.setClaimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setCreatedAt(LocalDateTime.now());

        Claim savedClaim = claimRepository.save(claim);
        return claimMapper.toDto(savedClaim);
    }

    @Override
    public ClaimDto getClaimById(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        return claimMapper.toDto(claim);
    }

    @Override
    public List<ClaimDto> getClaimsByClientId(UUID clientId) {
        return claimRepository.findByClientId(clientId).stream()
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaimDto> getClaimsByPolicyId(UUID policyId) {
        return claimRepository.findByPolicyId(policyId).stream()
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaimDto> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClaimDto updateClaim(UUID id, UpdateClaimRequest request) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        if (request.getDescription() != null) {
            claim.setDescription(request.getDescription());
        }
        if (request.getIncidentDate() != null) {
            claim.setIncidentDate(request.getIncidentDate());
        }
        if (request.getEstimatedAmount() != null) {
            claim.setEstimatedAmount(request.getEstimatedAmount());
        }
        if (request.getAssignedTo() != null) {
            claim.setAssignedTo(request.getAssignedTo());
        }

        Claim updatedClaim = claimRepository.save(claim);
        return claimMapper.toDto(updatedClaim);
    }

    @Override
    @Transactional
    public void submitClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.submit();
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    public void reviewClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.markAsUnderReview();
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    public void approveClaim(UUID id, BigDecimal approvedAmount, UUID approvedBy) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.approve(approvedAmount);
        claim.setApprovedBy(approvedBy);
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    public void rejectClaim(UUID id, String reason) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.reject(reason);
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    public void requestInfo(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.requestInfo();
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    public void closeClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.close();
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    public void deleteClaim(UUID id) {
        if (claimRepository.findById(id).isEmpty()) {
            throw new ClaimNotFoundException(id);
        }
        claimRepository.deleteById(id);
    }
}
