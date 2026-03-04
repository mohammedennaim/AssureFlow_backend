package com.pfe.policy.application.service.impl;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    @Override
    @Transactional
    public PolicyDto createPolicy(CreatePolicyRequest request) {
        Policy policy = policyMapper.toDomain(request);
        policy.setPolicyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        policy.setStatus(PolicyStatus.DRAFT);
        policy.setCreatedAt(LocalDateTime.now());

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

        // Publish domain event
        PolicyCreatedEvent event = PolicyCreatedEvent.builder()
                .policyId(savedPolicy.getId())
                .policyNumber(savedPolicy.getPolicyNumber())
                .clientId(savedPolicy.getClientId())
                .type(savedPolicy.getType() != null ? savedPolicy.getType().name() : null)
                .status(savedPolicy.getStatus() != null ? savedPolicy.getStatus().name() : null)
                .premiumAmount(savedPolicy.getPremiumAmount())
                .coverageAmount(savedPolicy.getCoverageAmount())
                .startDate(savedPolicy.getStartDate())
                .endDate(savedPolicy.getEndDate())
                .source("policy-service")
                .build();

        savedPolicy.registerEvent(event);
        log.info("PolicyCreatedEvent published for policy: {}", savedPolicy.getPolicyNumber());

        savedPolicy.clearDomainEvents();

        return policyMapper.toDto(savedPolicy);
    }

    @Override
    public PolicyDto getPolicyById(String id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        return policyMapper.toDto(policy);
    }

    @Override
    public List<PolicyDto> getPoliciesByClientId(String clientId) {
        return policyRepository.findByClientId(clientId).stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PolicyDto> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(policyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PolicyDto updatePolicy(String id, UpdatePolicyRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));

        if (request.getEndDate() != null) {
            policy.setEndDate(request.getEndDate());
        }
        if (request.getPremiumAmount() != null) {
            policy.setPremiumAmount(request.getPremiumAmount());
        }
        if (request.getCoverageAmount() != null) {
            policy.setCoverageAmount(request.getCoverageAmount());
        }
        if (request.getType() != null) {
            policy.setType(request.getType());
        }

        Policy updatedPolicy = policyRepository.save(policy);
        return policyMapper.toDto(updatedPolicy);
    }

    @Override
    @Transactional
    public void cancelPolicy(String id, String reason) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.cancel(reason);
        policyRepository.save(policy);
    }

    @Override
    @Transactional
    public void submitPolicy(String id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.submit();
        policyRepository.save(policy);
    }

    @Override
    @Transactional
    public void approvePolicy(String id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.approve();
        policyRepository.save(policy);
    }

    @Override
    @Transactional
    public void rejectPolicy(String id, String reason) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.reject(reason);
        policyRepository.save(policy);
    }

    @Override
    @Transactional
    public void expirePolicy(String id, String reason) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.expire(reason);
        policyRepository.save(policy);
    }

    @Override
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
        return policyMapper.toDto(savedPolicy);
    }
}
