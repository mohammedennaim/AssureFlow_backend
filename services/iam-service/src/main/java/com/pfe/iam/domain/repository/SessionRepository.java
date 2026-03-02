package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Session save(Session session);

    Optional<Session> findById(String id);

    Optional<Session> findByToken(String token);

    List<Session> findByUserId(String userId);

    void deleteById(String id);

    void deleteByUserId(String userId);

    void deleteExpiredSessions();
}
