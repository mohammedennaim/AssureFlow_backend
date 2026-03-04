package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.domain.model.Session;
import com.pfe.iam.domain.repository.SessionRepository;
import com.pfe.iam.infrastructure.persistence.entity.SessionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SessionRepositoryAdapter implements SessionRepository {

    private final JpaSessionRepository jpaSessionRepository;

    @Override
    public Session save(Session session) {
        SessionEntity entity = toEntity(session);
        SessionEntity saved = jpaSessionRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Session> findById(UUID id) {
        return jpaSessionRepository.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return jpaSessionRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public List<Session> findByUserId(UUID userId) {
        return jpaSessionRepository.findByUserId(userId.toString()).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaSessionRepository.deleteById(id.toString());
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaSessionRepository.deleteByUserId(userId.toString());
    }

    @Override
    @Transactional
    public void deleteExpiredSessions() {
        jpaSessionRepository.deleteExpiredSessions(LocalDateTime.now());
    }

    private Session toDomain(SessionEntity entity) {
        return Session.builder()
                .id(UUID.fromString(entity.getId()))
                .userId(UUID.fromString(entity.getUserId()))
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private SessionEntity toEntity(Session session) {
        return SessionEntity.builder()
                .id(session.getId() != null ? session.getId().toString() : null)
                .userId(session.getUserId() != null ? session.getUserId().toString() : null)
                .token(session.getToken())
                .expiresAt(session.getExpiresAt())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
