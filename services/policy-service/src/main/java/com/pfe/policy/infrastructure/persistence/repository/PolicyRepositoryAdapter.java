package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.domain.model.Policy;
import com.pfe.policy.domain.repository.PolicyRepository;
import com.pfe.policy.infrastructure.persistence.entity.PolicyEntity;
import com.pfe.policy.infrastructure.persistence.mapper.PolicyEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PolicyRepositoryAdapter implements PolicyRepository {

    private final JpaPolicyRepository jpaPolicyRepository;
    private final PolicyEntityMapper mapper;

    @Override
    public Policy save(Policy policy) {
        PolicyEntity entity = mapper.toEntity(policy);
        PolicyEntity savedEntity = jpaPolicyRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Policy> findById(String id) {
        return jpaPolicyRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Policy> findAll() {
        return jpaPolicyRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Policy> findByClientId(String clientId) {
        return jpaPolicyRepository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaPolicyRepository.deleteById(id);
    }

    @Override
    public boolean existsByPolicyNumber(String policyNumber) {
        return jpaPolicyRepository.existsByPolicyNumber(policyNumber);
    }
}
