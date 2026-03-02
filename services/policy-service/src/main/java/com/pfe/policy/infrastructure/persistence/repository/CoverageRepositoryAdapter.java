package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.domain.model.Coverage;
import com.pfe.policy.domain.repository.CoverageRepository;
import com.pfe.policy.infrastructure.persistence.entity.CoverageEntity;
import com.pfe.policy.infrastructure.persistence.entity.PolicyEntity;
import com.pfe.policy.infrastructure.persistence.mapper.PolicyEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoverageRepositoryAdapter implements CoverageRepository {

    private final JpaCoverageRepository jpaCoverageRepository;
    private final JpaPolicyRepository jpaPolicyRepository;
    private final PolicyEntityMapper mapper;

    @Override
    public Coverage save(Coverage coverage) {
        CoverageEntity entity = mapper.toEntity(coverage);
        // Explicitly set the policy reference to avoid detachment issues
        PolicyEntity policy = jpaPolicyRepository.findById(coverage.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        entity.setPolicy(policy);

        CoverageEntity savedEntity = jpaCoverageRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Coverage> findById(String id) {
        return jpaCoverageRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Coverage> findByPolicyId(String policyId) {
        return jpaCoverageRepository.findByPolicyId(policyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaCoverageRepository.deleteById(id);
    }
}
