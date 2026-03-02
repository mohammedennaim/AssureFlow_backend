package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUserId(String userId);
}
