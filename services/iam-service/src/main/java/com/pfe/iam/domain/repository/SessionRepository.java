package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {
    Session save(Session session);

    Optional<Session> findById(UUID id);

    Optional<Session> findByToken(String token);

    List<Session> findByUserId(UUID userId);

    void deleteById(UUID id);

    void deleteByUserId(UUID userId);

    void deleteExpiredSessions();
}
