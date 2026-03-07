package com.pfe.claims.application.service.impl;

import com.pfe.claims.application.dto.ClaimDto;
import com.pfe.claims.application.dto.CreateClaimRequest;
import com.pfe.claims.application.dto.UpdateClaimRequest;
import com.pfe.claims.application.mapper.ClaimMapper;
import com.pfe.claims.application.service.ClaimService;
import com.pfe.claims.domain.event.ClaimSubmittedEvent;
import com.pfe.claims.domain.exception.ClaimNotFoundException;
import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.model.ClaimStatus;
import com.pfe.claims.domain.repository.ClaimRepository;
import com.pfe.claims.infrastructure.client.PolicyDto;
import com.pfe.claims.infrastructure.client.PolicyServiceClient;
import com.pfe.commons.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final PolicyServiceClient policyServiceClient;

    @Override
    @Transactional
    public ClaimDto createClaim(CreateClaimRequest request) {
        validatePolicyExists(request.getPolicyId());

        Claim claim = claimMapper.toDomain(request);
        claim.setClaimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setCreatedAt(LocalDateTime.now());

        Claim savedClaim = claimRepository.save(claim);

        ClaimSubmittedEvent event = ClaimSubmittedEvent.builder()
                .claimId(savedClaim.getId())
                .claimNumber(savedClaim.getClaimNumber())
                .policyId(savedClaim.getPolicyId())
                .clientId(savedClaim.getClientId())
                .status(savedClaim.getStatus() != null ? savedClaim.getStatus().name() : null)
                .incidentDate(savedClaim.getIncidentDate())
                .estimatedAmount(savedClaim.getEstimatedAmount())
                .description(savedClaim.getDescription())
                .source("claims-service")
                .build();

        savedClaim.registerEvent(event);
        log.info("ClaimSubmittedEvent published for claim: {}", savedClaim.getClaimNumber());

        savedClaim.clearDomainEvents();

        return claimMapper.toDto(savedClaim);
    }

    private void validatePolicyExists(UUID policyId) {
        if (policyId == null) {
            throw new BusinessException("Policy ID is required");
        }
        try {
            PolicyDto policy = policyServiceClient.getPolicyById(policyId.toString());
            if (policy == null) {
                throw new BusinessException("Policy not found with ID: " + policyId);
            }
            if ("CANCELLED".equals(policy.getStatus()) || "EXPIRED".equals(policy.getStatus())) {
                throw new BusinessException("Cannot create claim for policy with status: " + policy.getStatus());
            }
            log.info("Policy validated: {} (status: {})", policy.getPolicyNumber(), policy.getStatus());
        } catch (BusinessException e) {
            throw e;
        } catch (feign.FeignException e) {
            log.error("Feign call failed to validate policy {}: {}", policyId, e.getMessage());
            throw new BusinessException("Unable to reach policy-service: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "claims", key = "#id")
    public ClaimDto getClaimById(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        return claimMapper.toDto(claim);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "claims", key = "'client:' + #clientId")
    public List<ClaimDto> getClaimsByClientId(UUID clientId) {
        return claimRepository.findByClientId(clientId).stream()
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "claims", key = "'policy:' + #policyId")
    public List<ClaimDto> getClaimsByPolicyId(UUID policyId) {
        return claimRepository.findByPolicyId(policyId).stream()
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDto> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "claims", allEntries = true)
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
    @CacheEvict(value = "claims", key = "#id")
    public void submitClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.submit();
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    @CacheEvict(value = "claims", key = "#id")
    public void reviewClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.markAsUnderReview();
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    @CacheEvict(value = "claims", key = "#id")
    public void approveClaim(UUID id, BigDecimal approvedAmount, UUID approvedBy) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.approve(approvedAmount);
        claim.setApprovedBy(approvedBy);
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    @CacheEvict(value = "claims", key = "#id")
    public void rejectClaim(UUID id, String reason) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.reject(reason);
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    @CacheEvict(value = "claims", key = "#id")
    public void requestInfo(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.requestInfo();
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    @CacheEvict(value = "claims", key = "#id")
    public void closeClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        claim.close();
        claimRepository.save(claim);
    }

    @Override
    @Transactional
    @CacheEvict(value = "claims", allEntries = true)
    public void deleteClaim(UUID id) {
        if (claimRepository.findById(id).isEmpty()) {
            throw new ClaimNotFoundException(id);
        }
        claimRepository.deleteById(id);
    }
}
