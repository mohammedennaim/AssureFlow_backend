package com.pfe.policy.application.service.impl;

import com.pfe.policy.application.dto.*;
import com.pfe.policy.application.service.PolicyService;
import com.pfe.policy.domain.exception.PolicyNotFoundException;
import com.pfe.policy.domain.model.Coverage;
import com.pfe.policy.domain.model.Policy;
import com.pfe.policy.domain.model.PolicyStatus;
import com.pfe.policy.domain.model.PolicyType;
import com.pfe.policy.domain.repository.PolicyRepository;
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
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    @Override
    @Transactional
    public PolicyResponse createPolicy(PolicyRequest request) {
        log.info("Creating new policy for client: {}", request.getClientId());

        String policyNumber = generatePolicyNumber();
        while (policyRepository.existsByPolicyNumber(policyNumber)) {
            policyNumber = generatePolicyNumber();
        }

        List<Coverage> coverages = request.getCoverages() == null ? List.of() :
                request.getCoverages().stream().map(c -> Coverage.builder()
                        .id(UUID.randomUUID().toString())
                        .type(c.getType())
                        .coverageLimit(c.getCoverageLimit())
                        .description(c.getDescription())
                        .build()).collect(Collectors.toList());

        Policy policy = Policy.builder()
                .id(UUID.randomUUID().toString())
                .policyNumber(policyNumber)
                .clientId(request.getClientId())
                .type(PolicyType.valueOf(request.getType()))
                .status(PolicyStatus.PENDING)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .premium(request.getPremium())
                .description(request.getDescription())
                .coverages(coverages)
                .build();

        Policy saved = policyRepository.save(policy);
        log.info("Policy created with number: {}", saved.getPolicyNumber());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyResponse getPolicyById(String id) {
        return toResponse(policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyResponse getPolicyByNumber(String policyNumber) {
        return toResponse(policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new PolicyNotFoundException(policyNumber)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponse> getPoliciesByClientId(String clientId) {
        return policyRepository.findByClientId(clientId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponse> getPoliciesByStatus(PolicyStatus status) {
        return policyRepository.findByStatus(status).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponse> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PolicyResponse updatePolicy(String id, PolicyRequest request) {
        log.info("Updating policy: {}", id);
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));

        List<Coverage> coverages = request.getCoverages() == null ? policy.getCoverages() :
                request.getCoverages().stream().map(c -> Coverage.builder()
                        .id(UUID.randomUUID().toString())
                        .type(c.getType())
                        .coverageLimit(c.getCoverageLimit())
                        .description(c.getDescription())
                        .build()).collect(Collectors.toList());

        policy.setType(PolicyType.valueOf(request.getType()));
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());
        policy.setPremium(request.getPremium());
        policy.setDescription(request.getDescription());
        policy.setCoverages(coverages);

        return toResponse(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponse cancelPolicy(String id) {
        log.info("Cancelling policy: {}", id);
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.cancel();
        return toResponse(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponse activatePolicy(String id) {
        log.info("Activating policy: {}", id);
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.activate();
        return toResponse(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public void deletePolicy(String id) {
        log.info("Deleting policy: {}", id);
        if (policyRepository.findById(id).isEmpty()) {
            throw new PolicyNotFoundException(id);
        }
        policyRepository.deleteById(id);
    }

    private String generatePolicyNumber() {
        return "POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PolicyResponse toResponse(Policy policy) {
        List<CoverageResponse> coverages = policy.getCoverages() == null ? List.of() :
                policy.getCoverages().stream().map(c -> CoverageResponse.builder()
                        .id(c.getId())
                        .type(c.getType())
                        .coverageLimit(c.getCoverageLimit())
                        .description(c.getDescription())
                        .build()).collect(Collectors.toList());

        return PolicyResponse.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .clientId(policy.getClientId())
                .type(policy.getType() != null ? policy.getType().name() : null)
                .status(policy.getStatus() != null ? policy.getStatus().name() : null)
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .premium(policy.getPremium())
                .description(policy.getDescription())
                .coverages(coverages)
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}
