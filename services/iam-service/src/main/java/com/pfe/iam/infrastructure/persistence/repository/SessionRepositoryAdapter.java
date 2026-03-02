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
    public Optional<Session> findById(String id) {
        return jpaSessionRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return jpaSessionRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public List<Session> findByUserId(String userId) {
        return jpaSessionRepository.findByUserId(userId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaSessionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        jpaSessionRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredSessions() {
        jpaSessionRepository.deleteExpiredSessions(LocalDateTime.now());
    }

    private Session toDomain(SessionEntity entity) {
        return Session.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private SessionEntity toEntity(Session session) {
        return SessionEntity.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .token(session.getToken())
                .expiresAt(session.getExpiresAt())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
