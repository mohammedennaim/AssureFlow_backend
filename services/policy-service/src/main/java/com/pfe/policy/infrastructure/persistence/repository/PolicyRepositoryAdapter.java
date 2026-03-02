package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.domain.model.Coverage;
import com.pfe.policy.domain.model.Policy;
import com.pfe.policy.domain.model.PolicyStatus;
import com.pfe.policy.domain.model.PolicyType;
import com.pfe.policy.domain.repository.PolicyRepository;
import com.pfe.policy.infrastructure.persistence.entity.CoverageEntity;
import com.pfe.policy.infrastructure.persistence.entity.PolicyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PolicyRepositoryAdapter implements PolicyRepository {

    private final JpaPolicyRepository jpaPolicyRepository;

    @Override
    public Policy save(Policy policy) {
        PolicyEntity entity = toEntity(policy);
        if (entity.getCoverages() != null) {
            entity.getCoverages().forEach(c -> c.setPolicy(entity));
        }
        PolicyEntity saved = jpaPolicyRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Policy> findById(String id) {
        return jpaPolicyRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Policy> findByPolicyNumber(String policyNumber) {
        return jpaPolicyRepository.findByPolicyNumber(policyNumber).map(this::toDomain);
    }

    @Override
    public List<Policy> findByClientId(String clientId) {
        return jpaPolicyRepository.findByClientId(clientId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Policy> findByStatus(PolicyStatus status) {
        return jpaPolicyRepository.findByStatus(status).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Policy> findAll() {
        return jpaPolicyRepository.findAll().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaPolicyRepository.deleteById(id);
    }

    @Override
    public boolean existsByPolicyNumber(String policyNumber) {
        return jpaPolicyRepository.existsByPolicyNumber(policyNumber);
    }

    private Policy toDomain(PolicyEntity entity) {
        List<Coverage> coverages = entity.getCoverages() == null ? List.of() :
                entity.getCoverages().stream().map(this::toCoverageDomain).collect(Collectors.toList());
        return Policy.builder()
                .id(entity.getId())
                .policyNumber(entity.getPolicyNumber())
                .clientId(entity.getClientId())
                .type(entity.getType())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .premium(entity.getPremium())
                .description(entity.getDescription())
                .coverages(coverages)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PolicyEntity toEntity(Policy domain) {
        List<CoverageEntity> coverages = domain.getCoverages() == null ? List.of() :
                domain.getCoverages().stream().map(this::toCoverageEntity).collect(Collectors.toList());
        return PolicyEntity.builder()
                .id(domain.getId())
                .policyNumber(domain.getPolicyNumber())
                .clientId(domain.getClientId())
                .type(domain.getType())
                .status(domain.getStatus())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .premium(domain.getPremium())
                .description(domain.getDescription())
                .coverages(coverages)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Coverage toCoverageDomain(CoverageEntity entity) {
        return Coverage.builder()
                .id(entity.getId())
                .type(entity.getType())
                .coverageLimit(entity.getCoverageLimit())
                .description(entity.getDescription())
                .build();
    }

    private CoverageEntity toCoverageEntity(Coverage domain) {
        return CoverageEntity.builder()
                .id(domain.getId())
                .type(domain.getType())
                .coverageLimit(domain.getCoverageLimit())
                .description(domain.getDescription())
                .build();
    }
}
