package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.domain.model.ClaimAssessment;
import com.pfe.claims.domain.repository.ClaimAssessmentRepository;
import com.pfe.claims.infrastructure.persistence.entity.ClaimAssessmentEntity;
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
public class ClaimAssessmentRepositoryAdapter implements ClaimAssessmentRepository {

    private final JpaClaimAssessmentRepository jpaClaimAssessmentRepository;
    private final JpaClaimRepository jpaClaimRepository;
    private final ClaimEntityMapper mapper;

    @Override
    public ClaimAssessment save(ClaimAssessment assessment) {
        ClaimAssessmentEntity entity = mapper.toEntity(assessment);
        ClaimEntity claim = jpaClaimRepository.findById(assessment.getClaimId())
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        entity.setClaim(claim);

        ClaimAssessmentEntity savedEntity = jpaClaimAssessmentRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ClaimAssessment> findById(UUID id) {
        return jpaClaimAssessmentRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ClaimAssessment> findByClaimId(UUID claimId) {
        return jpaClaimAssessmentRepository.findByClaimId(claimId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaClaimAssessmentRepository.deleteById(id);
    }
}
