package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.SessionDto;
import com.pfe.iam.application.service.SessionService;
import com.pfe.iam.domain.model.Session;
import com.pfe.iam.domain.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public SessionDto createSession(String userId, String token, long expirationMs) {
        Session session = Session.builder()
                .userId(UUID.fromString(userId))
                .token(token)
                .expiresAt(LocalDateTime.now().plusNanos(expirationMs * 1_000_000))
                .createdAt(LocalDateTime.now())
                .build();

        Session saved = sessionRepository.save(session);
        log.info("Session created for user: {}", userId);
        return toDto(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public List<SessionDto> getSessionsByUserId(String userId) {
        return sessionRepository.findByUserId(UUID.fromString(userId)).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional
    public void invalidateSession(String sessionId) {
        sessionRepository.deleteById(UUID.fromString(sessionId));
        log.info("Session invalidated: {}", sessionId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void invalidateAllUserSessions(String userId) {
        sessionRepository.deleteByUserId(UUID.fromString(userId));
        log.info("All sessions invalidated for user: {}", userId);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void cleanupExpiredSessions() {
        sessionRepository.deleteExpiredSessions();
        log.info("Expired sessions cleaned up");
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    public boolean isSessionValid(String token) {
        return sessionRepository.findByToken(token)
                .map(session -> !session.isExpired())
                .orElse(false);
    }

    private SessionDto toDto(Session session) {
        return SessionDto.builder()
                .id(session.getId() != null ? session.getId().toString() : null)
                .userId(session.getUserId() != null ? session.getUserId().toString() : null)
                .expiresAt(session.getExpiresAt())
                .createdAt(session.getCreatedAt())
                .expired(session.isExpired())
                .build();
    }
}
