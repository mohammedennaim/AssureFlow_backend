package com.pfe.claims.infrastructure.persistence.repository;

import com.pfe.claims.domain.model.Payout;
import com.pfe.claims.domain.repository.PayoutRepository;
import com.pfe.claims.infrastructure.persistence.entity.ClaimEntity;
import com.pfe.claims.infrastructure.persistence.entity.PayoutEntity;
import com.pfe.claims.infrastructure.persistence.mapper.ClaimEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PayoutRepositoryAdapter implements PayoutRepository {

    private final JpaPayoutRepository jpaPayoutRepository;
    private final JpaClaimRepository jpaClaimRepository;
    private final ClaimEntityMapper mapper;

    @Override
    public Payout save(Payout payout) {
        PayoutEntity entity = mapper.toEntity(payout);
        ClaimEntity claim = jpaClaimRepository.findById(payout.getClaimId())
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
        entity.setClaim(claim);

        PayoutEntity savedEntity = jpaPayoutRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payout> findById(UUID id) {
        return jpaPayoutRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Payout> findByClaimId(UUID claimId) {
        return jpaPayoutRepository.findByClaimId(claimId).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaPayoutRepository.deleteById(id);
    }
}
