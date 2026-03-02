package com.pfe.policy.infrastructure.persistence.repository;

import com.pfe.policy.domain.model.Beneficiary;
import com.pfe.policy.domain.repository.BeneficiaryRepository;
import com.pfe.policy.infrastructure.persistence.entity.BeneficiaryEntity;
import com.pfe.policy.infrastructure.persistence.entity.PolicyEntity;
import com.pfe.policy.infrastructure.persistence.mapper.PolicyEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BeneficiaryRepositoryAdapter implements BeneficiaryRepository {

    private final JpaBeneficiaryRepository jpaBeneficiaryRepository;
    private final JpaPolicyRepository jpaPolicyRepository;
    private final PolicyEntityMapper mapper;

    @Override
    public Beneficiary save(Beneficiary beneficiary) {
        BeneficiaryEntity entity = mapper.toEntity(beneficiary);
        PolicyEntity policy = jpaPolicyRepository.findById(beneficiary.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        entity.setPolicy(policy);

        BeneficiaryEntity savedEntity = jpaBeneficiaryRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Beneficiary> findById(String id) {
        return jpaBeneficiaryRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Beneficiary> findByPolicyId(String policyId) {
        return jpaBeneficiaryRepository.findByPolicyId(policyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaBeneficiaryRepository.deleteById(id);
    }
}
