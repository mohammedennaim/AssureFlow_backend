package com.pfe.notification.infrastructure.persistence.repository;

import com.pfe.notification.domain.model.Notification;
import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationStatus;
import com.pfe.notification.domain.model.NotificationType;
import com.pfe.notification.domain.repository.NotificationRepository;
import com.pfe.notification.infrastructure.persistence.mapper.NotificationEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final JpaNotificationRepository jpaRepository;
    private final NotificationEntityMapper mapper;

    @Override
    public Notification save(Notification notification) {
        var entity = mapper.toEntity(notification);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Notification> findByPolicyId(UUID policyId) {
        return jpaRepository.findByPolicyId(policyId).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByRecipient(String recipient) {
        return jpaRepository.findByRecipient(recipient).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByType(NotificationType type) {
        return jpaRepository.findByType(type).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByStatus(NotificationStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<Notification> findAllPaged(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public long countByRecipientAndReadFalse(String recipient) {
        return jpaRepository.countByRecipientAndReadFalse(recipient);
    }

    @Override
    public List<Notification> saveAll(List<Notification> notifications) {
        var entities = notifications.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        var saved = jpaRepository.saveAll(entities);
        return saved.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByStatus(NotificationStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public long countByChannel(NotificationChannel channel) {
        return jpaRepository.countByChannel(channel);
    }

    @Override
    public long countByCreatedAtAfter(LocalDateTime date) {
        return jpaRepository.countByCreatedAtAfter(date);
    }

    @Override
    public List<Notification> findByChannel(NotificationChannel channel) {
        return jpaRepository.findByChannel(channel).stream()
                .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<Notification> findByChannel(NotificationChannel channel, Pageable pageable) {
        return jpaRepository.findByChannel(channel, pageable).map(mapper::toDomain);
    }
}
