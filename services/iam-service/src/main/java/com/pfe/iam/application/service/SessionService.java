package com.pfe.iam.application.service;

import com.pfe.iam.application.dto.SessionDto;

import java.util.List;

public interface SessionService {
    SessionDto createSession(String userId, String token, long expirationMs);

    List<SessionDto> getSessionsByUserId(String userId);

    void invalidateSession(String sessionId);

    void invalidateAllUserSessions(String userId);

    void cleanupExpiredSessions();

    boolean isSessionValid(String token);
}
