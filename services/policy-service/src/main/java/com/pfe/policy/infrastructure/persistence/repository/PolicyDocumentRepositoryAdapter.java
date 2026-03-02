package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.domain.model.PolicyDocument;
import com.pfe.policy.domain.repository.PolicyDocumentRepository;
import com.pfe.policy.infrastructure.persistence.entity.PolicyDocumentEntity;
import com.pfe.policy.infrastructure.persistence.entity.PolicyEntity;
import com.pfe.policy.infrastructure.persistence.mapper.PolicyEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PolicyDocumentRepositoryAdapter implements PolicyDocumentRepository {

    private final JpaPolicyDocumentRepository jpaPolicyDocumentRepository;
    private final JpaPolicyRepository jpaPolicyRepository;
    private final PolicyEntityMapper mapper;

    @Override
    public PolicyDocument save(PolicyDocument document) {
        PolicyDocumentEntity entity = mapper.toEntity(document);
        PolicyEntity policy = jpaPolicyRepository.findById(document.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        entity.setPolicy(policy);

        PolicyDocumentEntity savedEntity = jpaPolicyDocumentRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PolicyDocument> findById(String id) {
        return jpaPolicyDocumentRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PolicyDocument> findByPolicyId(String policyId) {
        return jpaPolicyDocumentRepository.findByPolicyId(policyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaPolicyDocumentRepository.deleteById(id);
    }
}
