package com.pfe.policy.application.service.impl;

import com.pfe.policy.application.dto.CreatePolicyRequest;
import com.pfe.policy.application.dto.PolicyDto;
import com.pfe.policy.application.dto.UpdatePolicyRequest;
import com.pfe.policy.application.mapper.PolicyMapper;
import com.pfe.policy.application.service.PolicyService;
import com.pfe.policy.domain.exception.PolicyNotFoundException;
import com.pfe.policy.domain.model.Policy;
import com.pfe.policy.domain.model.PolicyStatus;
import com.pfe.policy.domain.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        if (policy.getCoverages() != null) {
            policy.getCoverages().forEach(c -> c.setPolicyId(policy.getId()));
        }
        if (policy.getBeneficiaries() != null) {
            policy.getBeneficiaries().forEach(b -> b.setPolicyId(policy.getId()));
        }

        Policy savedPolicy = policyRepository.save(policy);
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

        Policy updatedPolicy = policyRepository.save(policy);
        return policyMapper.toDto(updatedPolicy);
    }

    @Override
    @Transactional
    public void cancelPolicy(String id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
        policy.cancelPolicy();
        policyRepository.save(policy);
    }

    @Override
    @Transactional
    public PolicyDto renewPolicy(String id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));

        // Simple logic for renewal: duplicate and set new dates
        Policy newPolicy = Policy.builder()
                .clientId(policy.getClientId())
                .policyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .type(policy.getType())
                .status(PolicyStatus.DRAFT)
                .premiumAmount(policy.getPremiumAmount())
                .startDate(policy.getEndDate().plusDays(1))
                .endDate(policy.getEndDate().plusYears(1))
                .coverages(policy.getCoverages())
                .beneficiaries(policy.getBeneficiaries())
                .build();

        policy.renewPolicy(newPolicy);

        Policy savedPolicy = policyRepository.save(newPolicy);
        return policyMapper.toDto(savedPolicy);
    }
}
