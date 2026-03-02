package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.infrastructure.persistence.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, String> {
    Optional<PasswordResetTokenEntity> findByToken(String token);

    void deleteByUserId(String userId);
}
