package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.domain.model.ClaimDocument;
import com.pfe.claims.domain.repository.ClaimDocumentRepository;
import com.pfe.claims.infrastructure.persistence.entity.ClaimDocumentEntity;
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
public class ClaimDocumentRepositoryAdapter implements ClaimDocumentRepository {

    private final JpaClaimDocumentRepository jpaClaimDocumentRepository;
    private final JpaClaimRepository jpaClaimRepository;
    private final ClaimEntityMapper mapper;

    @Override
    public ClaimDocument save(ClaimDocument document) {
        ClaimDocumentEntity entity = mapper.toEntity(document);
        ClaimEntity claim = jpaClaimRepository.findById(document.getClaimId())
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        entity.setClaim(claim);

        ClaimDocumentEntity savedEntity = jpaClaimDocumentRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ClaimDocument> findById(UUID id) {
        return jpaClaimDocumentRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ClaimDocument> findByClaimId(UUID claimId) {
        return jpaClaimDocumentRepository.findByClaimId(claimId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaClaimDocumentRepository.deleteById(id);
    }
}
