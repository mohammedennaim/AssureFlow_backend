package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.repository.ClaimRepository;
import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import com.pfe.claims.infrastructure.persistence.mapper.ClaimEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClaimRepositoryAdapter implements ClaimRepository {

    private final JpaClaimRepository jpaClaimRepository;
    private final ClaimEntityMapper mapper;

    @Override
    public Claim save(Claim claim) {
        ClaimEntity entity = mapper.toEntity(claim);
        ClaimEntity savedEntity = jpaClaimRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Claim> findById(UUID id) {
        return jpaClaimRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Claim> findAll() {
        return jpaClaimRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Claim> findByClientId(UUID clientId) {
        return jpaClaimRepository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Claim> findByPolicyId(UUID policyId) {
        return jpaClaimRepository.findByPolicyId(policyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaClaimRepository.deleteById(id);
    }

    @Override
    public boolean existsByClaimNumber(String claimNumber) {
        return jpaClaimRepository.existsByClaimNumber(claimNumber);
    }
}
