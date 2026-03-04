package com.pfe.notification.infrastructure.persistence.repository;

import com.pfe.notification.domain.model.NotificationLog;
import com.pfe.notification.domain.repository.NotificationLogRepository;
import com.pfe.notification.infrastructure.persistence.mapper.NotificationLogEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationLogRepositoryAdapter implements NotificationLogRepository {

    private final JpaNotificationLogRepository jpaRepository;
    private final NotificationLogEntityMapper mapper;

    @Override
    public NotificationLog save(NotificationLog log) {
        var entity = mapper.toEntity(log);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<NotificationLog> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<NotificationLog> findByNotificationId(UUID notificationId) {
        return jpaRepository.findByNotificationId(notificationId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<NotificationLog> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
