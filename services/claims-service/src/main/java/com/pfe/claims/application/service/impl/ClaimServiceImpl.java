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
import com.pfe.claims.infrastructure.client.ClientDto;
import com.pfe.claims.infrastructure.client.PolicyDto;
import com.pfe.claims.infrastructure.client.PolicyServiceClient;
import com.pfe.claims.infrastructure.messaging.ClaimEventPublisher;
import com.pfe.commons.dto.BaseResponse;
import com.pfe.commons.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final com.pfe.claims.infrastructure.client.ClientServiceClient clientServiceClient;
    private final ClaimEventPublisher claimEventPublisher;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public ClaimDto createClaim(CreateClaimRequest request) {
        validatePolicyExists(request.getPolicyId());

        Claim claim = claimMapper.toDomain(request);
        claim.setClaimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        claim.setStatus(ClaimStatus.SUBMITTED);
        
        LocalDateTime now = LocalDateTime.now();
        claim.setCreatedAt(now);
        
        // Set SLA deadline to 48 hours from creation
        claim.setSlaDeadline(now.plusHours(48));

        Claim savedClaim = claimRepository.save(claim);

        // Publish event for SLA tracking
        claimEventPublisher.publishClaimCreated(
            savedClaim.getId(),
            savedClaim.getPolicyId(),
            savedClaim.getClientId(),
            savedClaim.getCreatedAt(),
            savedClaim.getSlaDeadline()
        );

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
        log.info("ClaimSubmittedEvent published for claim: {} with SLA deadline: {}", 
                savedClaim.getClaimNumber(), savedClaim.getSlaDeadline());

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
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public ClaimDto getClaimById(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        return claimMapper.toDto(claim);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public List<ClaimDto> getClaimsByClientId(UUID clientId) {
        return claimRepository.findByClientId(clientId).stream()
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public List<ClaimDto> getClaimsByPolicyId(UUID policyId) {
        return claimRepository.findByPolicyId(policyId).stream()
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional(readOnly = true)
    public List<ClaimDto> getAllClaims() {
        return claimRepository.findAll().stream()
                .filter(claim -> !claim.isArchivedByAdmin())
                .map(claimMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public ClaimDto updateClaim(UUID id, UpdateClaimRequest request) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        log.info("Updating claim {} with request: {}", id, request);

        claim.setDescription(request.getDescription());
        claim.setIncidentDate(request.getIncidentDate());
        claim.setEstimatedAmount(request.getEstimatedAmount());
        claim.setAssignedTo(request.getAssignedTo());

        Claim updatedClaim = claimRepository.save(claim);
        log.info("Claim {} updated successfully", id);
        return claimMapper.toDto(updatedClaim);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public void submitClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        
        String oldStatus = claim.getStatus() != null ? claim.getStatus().name() : null;
        claim.submit();
        claimRepository.save(claim);
        
        // Publish status change event if status changed
        if (!ClaimStatus.SUBMITTED.name().equals(oldStatus)) {
            claimEventPublisher.publishClaimStatusChanged(id, oldStatus, "SUBMITTED", null);
            log.info("Claim {} status changed to SUBMITTED, event published", claim.getClaimNumber());
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void reviewClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        
        String oldStatus = claim.getStatus() != null ? claim.getStatus().name() : null;
        claim.markAsUnderReview();
        claimRepository.save(claim);
        
        // Publish status change event
        claimEventPublisher.publishClaimStatusChanged(id, oldStatus, "UNDER_REVIEW", null);
        log.info("Claim {} status changed to UNDER_REVIEW, event published", claim.getClaimNumber());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void approveClaim(UUID id, BigDecimal approvedAmount, UUID approvedBy) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        String oldStatus = claim.getStatus() != null ? claim.getStatus().name() : null;
        claim.approve(approvedAmount);
        claim.setApprovedBy(approvedBy);
        claimRepository.save(claim);

        // Fetch client data for notification
        ClientDto clientDto = fetchClientData(claim.getPolicyId());

        // Publish status change event with full data
        claimEventPublisher.publishClaimApproved(
            id,
            claim.getClaimNumber(),
            claim.getClientId() != null ? claim.getClientId().toString() : null,
            clientDto != null ? clientDto.getEmail() : null,
            clientDto != null ? clientDto.getPhone() : null,
            approvedAmount
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void rejectClaim(UUID id, String reason) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        String oldStatus = claim.getStatus() != null ? claim.getStatus().name() : null;
        claim.reject(reason);
        claimRepository.save(claim);

        // Fetch client data for notification
        ClientDto clientDto = fetchClientData(claim.getPolicyId());

        // Publish status change event with full data
        claimEventPublisher.publishClaimRejected(
            id,
            claim.getClaimNumber(),
            claim.getClientId() != null ? claim.getClientId().toString() : null,
            clientDto != null ? clientDto.getEmail() : null,
            clientDto != null ? clientDto.getPhone() : null,
            reason
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void requestInfo(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        
        String oldStatus = claim.getStatus() != null ? claim.getStatus().name() : null;
        claim.requestInfo();
        claimRepository.save(claim);
        
        // Publish status change event
        claimEventPublisher.publishClaimStatusChanged(id, oldStatus, "INFO_REQUESTED", null);
        log.info("Claim {} status changed to INFO_REQUESTED, event published", claim.getClaimNumber());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void markAsPaid(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        String oldStatus = claim.getStatus() != null ? claim.getStatus().name() : null;
        claim.markAsPaid();
        claimRepository.save(claim);

        // Fetch client data for notification
        ClientDto clientDto = fetchClientData(claim.getPolicyId());

        // Publish status change event with full data
        claimEventPublisher.publishClaimPaid(
            id,
            claim.getClaimNumber(),
            claim.getClientId() != null ? claim.getClientId().toString() : null,
            clientDto != null ? clientDto.getEmail() : null,
            clientDto != null ? clientDto.getPhone() : null,
            claim.getApprovedAmount()
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void closeClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        String oldStatus = claim.getStatus() != null ? claim.getStatus().name() : null;
        claim.close();
        claimRepository.save(claim);

        // Publish status change event
        claimEventPublisher.publishClaimStatusChanged(id, oldStatus, "CLOSED", null);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void archiveClaim(UUID id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        String oldStatus = claim.getStatus() != null ? claim.getStatus().name() : null;
        boolean wasClosed = claim.getStatus() == ClaimStatus.CLOSED;

        claim.archiveByAdmin();
        claimRepository.save(claim);

        if (!wasClosed) {
            claimEventPublisher.publishClaimStatusChanged(id, oldStatus, "CLOSED", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteClaim(UUID id) {
        if (claimRepository.findById(id).isEmpty()) {
            throw new ClaimNotFoundException(id);
        }
        claimRepository.deleteById(id);
    }

    /**
     * Fetches client data from policy for Kafka notifications.
     */
    private ClientDto fetchClientData(UUID policyId) {
        try {
            if (policyId == null) {
                return null;
            }
            PolicyDto policy = policyServiceClient.getPolicyById(policyId.toString());
            if (policy == null || policy.getClientId() == null) {
                return null;
            }
            BaseResponse<ClientDto> response = clientServiceClient.getClientById(policy.getClientId());
            if (response != null && response.isSuccess()) {
                return response.getData();
            }
            return null;
        } catch (Exception e) {
            log.warn("[KAFKA] Could not fetch client data for notification: {}", e.getMessage());
            return null;
        }
    }
}
