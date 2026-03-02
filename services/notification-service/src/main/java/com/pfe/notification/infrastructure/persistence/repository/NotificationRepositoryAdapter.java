package com.pfe.notification.infrastructure.persistence.repository;

import com.pfe.notification.domain.model.Notification;
import com.pfe.notification.domain.repository.NotificationRepository;
import com.pfe.notification.infrastructure.persistence.entity.NotificationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final JpaNotificationRepository jpaNotificationRepository;

    @Override
    public Notification save(Notification notification) {
        return toDomain(jpaNotificationRepository.save(toEntity(notification)));
    }

    @Override
    public Optional<Notification> findById(String id) {
        return jpaNotificationRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Notification> findByRecipientId(String recipientId) {
        return jpaNotificationRepository.findByRecipientId(recipientId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByRecipientId(String recipientId) {
        return jpaNotificationRepository.findByRecipientIdAndReadFalse(recipientId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findAll() {
        return jpaNotificationRepository.findAll().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaNotificationRepository.deleteById(id);
    }

    private Notification toDomain(NotificationEntity entity) {
        return Notification.builder()
                .id(entity.getId())
                .recipientId(entity.getRecipientId())
                .type(entity.getType())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .build();
    }

    private NotificationEntity toEntity(Notification domain) {
        return NotificationEntity.builder()
                .id(domain.getId())
                .recipientId(domain.getRecipientId())
                .type(domain.getType())
                .subject(domain.getSubject())
                .message(domain.getMessage())
                .read(domain.isRead())
                .createdAt(domain.getCreatedAt())
                .readAt(domain.getReadAt())
                .build();
    }
}
