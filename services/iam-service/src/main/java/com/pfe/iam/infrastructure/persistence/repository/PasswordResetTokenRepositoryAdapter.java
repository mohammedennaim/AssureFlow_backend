package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.domain.model.PasswordResetToken;
import com.pfe.iam.domain.repository.PasswordResetTokenRepository;
import com.pfe.iam.infrastructure.persistence.entity.PasswordResetTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final JpaPasswordResetTokenRepository jpaRepository;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenEntity entity = toEntity(token);
        PasswordResetTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        jpaRepository.deleteByUserId(userId);
    }

    private PasswordResetTokenEntity toEntity(PasswordResetToken domain) {
        return PasswordResetTokenEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .token(domain.getToken())
                .expiresAt(domain.getExpiresAt())
                .used(domain.isUsed())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    private PasswordResetToken toDomain(PasswordResetTokenEntity entity) {
        return PasswordResetToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .used(entity.isUsed())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
