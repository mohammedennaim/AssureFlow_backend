package com.pfe.policy.application.service.impl;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.commons.exceptions.BusinessException;
import com.pfe.policy.application.dto.CreatePolicyRequest;
import com.pfe.policy.application.dto.PolicyDto;
import com.pfe.policy.application.dto.UpdatePolicyRequest;
import com.pfe.policy.application.mapper.PolicyMapper;
import com.pfe.policy.application.service.PolicyService;
import com.pfe.policy.domain.event.PolicyCreatedEvent;
import com.pfe.policy.domain.exception.PolicyNotFoundException;
import com.pfe.policy.domain.model.Policy;
import com.pfe.policy.domain.model.PolicyStatus;
import com.pfe.policy.domain.repository.PolicyRepository;
import com.pfe.policy.infrastructure.client.ClientDto;
import com.pfe.policy.infrastructure.client.ClientServiceClient;
import com.pfe.policy.infrastructure.messaging.PolicyEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    private final ClientServiceClient clientServiceClient;
    private final PolicyEventPublisher policyEventPublisher;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public PolicyDto createPolicy(CreatePolicyRequest request) {
        ClientDto client = validateClientExists(request.getClientId());

        Policy policy = policyMapper.toDomain(request);
        policy.setPolicyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        policy.setStatus(PolicyStatus.ACTIVE);

        if (policy.getCoverageAmount() != null) {
            policy.calculatePremium();
        }

        if (policy.getCoverages() != null) {
            policy.getCoverages().forEach(c -> c.setPolicyId(policy.getId()));
        }
        if (policy.getBeneficiaries() != null) {
            policy.getBeneficiaries().forEach(b -> b.setPolicyId(policy.getId()));
        }

        Policy savedPolicy = policyRepository.save(policy);

        PolicyCreatedEvent event = PolicyCreatedEvent.builder()
                .policyId(savedPolicy.getId())
                .policyNumber(savedPolicy.getPolicyNumber())
                .clientId(savedPolicy.getClientId())
                .clientEmail(client != null ? client.getEmail() : null)
                .clientPhone(client != null ? client.getPhone() : null)
                .type(savedPolicy.getType() != null ? savedPolicy.getType().name() : null)
                .status(savedPolicy.getStatus() != null ? savedPolicy.getStatus().name() : null)
                .premiumAmount(savedPolicy.getPremiumAmount())
                .coverageAmount(savedPolicy.getCoverageAmount())
                .startDate(savedPolicy.getStartDate())
                .endDate(savedPolicy.getEndDate())
                .source("policy-service")
                .build();

        savedPolicy.registerEvent(event);
        
        policyEventPublisher.publishPolicyCreated(event);
        log.info("PolicyCreatedEvent published to Kafka for policy: {}", savedPolicy.getPolicyNumber());

        savedPolicy.clearDomainEvents();

        return policyMapper.toDto(savedPolicy);
    }

    private ClientDto validateClientExists(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new BusinessException("Client ID is required");
        }
        try {
            BaseResponse<ClientDto> response = clientServiceClient.getClientById(clientId);
            if (response == null || !response.isSuccess() || response.getData() == null) {
                throw new BusinessException("Client not found with ID: " + clientId);
            }
            log.info("Client validated successfully for ID: {}", clientId);
            return response.getData();
        } catch (BusinessException e) {
            throw e;
        } catch (feign.FeignException e) {
            log.error("Feign call failed to validate client {}: {}", clientId, e.getMessage());
            throw new BusinessException("Unable to reach client-service: " + e.getMessage());
        }
    }

    private ClientDto getClientSafely(String clientId) {
        try {
            BaseResponse<ClientDto> response = clientServiceClient.getClientById(clientId);
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch client details for {}: {}", clientId, e.getMessage());
        }
        return null;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public PolicyDto getPolicyById(String id) {
        log.info("Fetching policy from database: {}", id);
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        return policyMapper.toDto(policy);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public List<PolicyDto> getPoliciesByClientId(String clientId) {
        log.info("Fetching policies from database for client: {}", clientId);
        return policyRepository.findByClientId(clientId).stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional(readOnly = true)
    public List<PolicyDto> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional(readOnly = true)
    public Page<PolicyDto> getAllPolicies(Pageable pageable) {
        return policyRepository.findAll(pageable)
                .map(policyMapper::toDto);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public PolicyDto updatePolicy(String id, UpdatePolicyRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));

        log.info("Updating policy {}: current status={}, request={}", id, policy.getStatus(), request);

        if (request.getClientId() != null && !request.getClientId().equals(policy.getClientId())) {
            validateClientExists(request.getClientId());
            policy.setClientId(request.getClientId());
        }
        if (request.getType() != null) {
            policy.setType(request.getType());
        }
        if (request.getStartDate() != null) {
            policy.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            policy.setEndDate(request.getEndDate());
        }
        if (request.getCoverageAmount() != null) {
            policy.setCoverageAmount(request.getCoverageAmount());
            if (request.getPremiumAmount() == null) {
                policy.calculatePremium();
                log.info("Recalculated premium for policy {} because coverage changed: {}", id, policy.getPremiumAmount());
            }
        }
        if (request.getPremiumAmount() != null) {
            policy.setPremiumAmount(request.getPremiumAmount());
        }

        policy.validateDates();
        policy.validateCoverageAmount();

        Policy updatedPolicy = policyRepository.save(policy);
        log.info("Policy {} updated successfully", id);
        return policyMapper.toDto(updatedPolicy);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public void cancelPolicy(String id, String reason) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.cancel(reason);
        policyRepository.save(policy);
        
        policyEventPublisher.publishPolicyEvent("policy.cancelled", buildPolicyEventMap(policy, reason));
        log.info("Policy cancelled event published for policy: {}", policy.getPolicyNumber());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public void submitPolicy(String id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.submit();
        policyRepository.save(policy);
        
        policyEventPublisher.publishPolicyEvent("policy.submitted", buildPolicyEventMap(policy));
        log.info("Policy submitted event published for policy: {}", policy.getPolicyNumber());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void approvePolicy(String id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.approve();
        policyRepository.save(policy);
        
        policyEventPublisher.publishPolicyEvent("policy.approved", buildPolicyEventMap(policy));
        log.info("Policy approved event published for policy: {}", policy.getPolicyNumber());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void rejectPolicy(String id, String reason) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.reject(reason);
        policyRepository.save(policy);
        
        policyEventPublisher.publishPolicyEvent("policy.rejected", buildPolicyEventMap(policy, reason));
        log.info("Policy rejected event published for policy: {}", policy.getPolicyNumber());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void expirePolicy(String id, String reason) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.expire(reason);
        policyRepository.save(policy);
        
        policyEventPublisher.publishPolicyEvent("policy.expiring", buildPolicyEventMap(policy, reason));
        log.info("Policy expiring event published for policy: {}", policy.getPolicyNumber());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public PolicyDto renewPolicy(String id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));

        Policy newPolicy = Policy.builder()
                .clientId(policy.getClientId())
                .policyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .type(policy.getType())
                .status(PolicyStatus.DRAFT)
                .premiumAmount(policy.getPremiumAmount())
                .coverageAmount(policy.getCoverageAmount())
                .startDate(policy.getEndDate().plusDays(1))
                .endDate(policy.getEndDate().plusYears(1))
                .createdAt(LocalDateTime.now())
                .coverages(policy.getCoverages())
                .beneficiaries(policy.getBeneficiaries())
                .build();

        policy.renewPolicy(newPolicy);

        Policy savedPolicy = policyRepository.save(newPolicy);

        // Publish policy.renewed event
        policyEventPublisher.publishPolicyEvent("policy.renewed", buildPolicyEventMap(savedPolicy));
        log.info("Policy renewed event published for policy: {}", savedPolicy.getPolicyNumber());

        return policyMapper.toDto(savedPolicy);
    }

    private java.util.Map<String, Object> buildPolicyEventMap(Policy policy) {
        return buildPolicyEventMap(policy, null);
    }

    private java.util.Map<String, Object> buildPolicyEventMap(Policy policy, String reason) {
        java.util.Map<String, Object> eventMap = new java.util.HashMap<>();
        ClientDto client = getClientSafely(policy.getClientId());
        eventMap.put("policyId", policy.getId());
        eventMap.put("policyNumber", policy.getPolicyNumber());
        eventMap.put("clientId", policy.getClientId());
        eventMap.put("clientEmail", client != null ? client.getEmail() : null);
        eventMap.put("clientPhone", client != null ? client.getPhone() : null);
        eventMap.put("type", policy.getType() != null ? policy.getType().name() : null);
        eventMap.put("status", policy.getStatus() != null ? policy.getStatus().name() : null);
        eventMap.put("premiumAmount", policy.getPremiumAmount());
        eventMap.put("coverageAmount", policy.getCoverageAmount());
        eventMap.put("startDate", policy.getStartDate());
        eventMap.put("endDate", policy.getEndDate());
        if (reason != null) {
            if (policy.getStatus() == com.pfe.policy.domain.model.PolicyStatus.CANCELLED) {
                eventMap.put("cancellationReason", reason);
            } else {
                eventMap.put("rejectionReason", reason);
            }
        }
        return eventMap;
    }
}
