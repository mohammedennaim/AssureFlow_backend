package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.domain.model.ClaimPayout;
import com.pfe.claims.domain.repository.ClaimPayoutRepository;
import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import com.pfe.claims.infrastructure.persistence.entity.ClaimPayoutEntity;
import com.pfe.claims.infrastructure.persistence.mapper.ClaimEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClaimPayoutRepositoryAdapter implements ClaimPayoutRepository {

    private final JpaClaimPayoutRepository jpaClaimPayoutRepository;
    private final JpaClaimRepository jpaClaimRepository;
    private final ClaimEntityMapper mapper;

    @Override
    public ClaimPayout save(ClaimPayout payout) {
        ClaimPayoutEntity entity = mapper.toEntity(payout);
        ClaimEntity claim = jpaClaimRepository.findById(payout.getClaimId())
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        entity.setClaim(claim);

        ClaimPayoutEntity savedEntity = jpaClaimPayoutRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ClaimPayout> findById(UUID id) {
        return jpaClaimPayoutRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ClaimPayout> findByClaimId(UUID claimId) {
        return jpaClaimPayoutRepository.findByClaimId(claimId).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaClaimPayoutRepository.deleteById(id);
    }
}
