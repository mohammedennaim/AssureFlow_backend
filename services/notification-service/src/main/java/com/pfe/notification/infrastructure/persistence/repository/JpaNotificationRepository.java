package com.pfe.notification.infrastructure.persistence.repository;

import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationStatus;
import com.pfe.notification.domain.model.NotificationType;
import com.pfe.notification.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByPolicyId(UUID policyId);

    List<NotificationEntity> findByRecipient(String recipient);

    List<NotificationEntity> findByType(NotificationType type);

    List<NotificationEntity> findByStatus(NotificationStatus status);

    long countByRecipientAndReadFalse(String recipient);

    // Statistics methods
    long countByStatus(NotificationStatus status);

    long countByChannel(NotificationChannel channel);

    long countByCreatedAtAfter(LocalDateTime date);

    List<NotificationEntity> findByChannel(NotificationChannel channel);

    Page<NotificationEntity> findByChannel(NotificationChannel channel, Pageable pageable);
}
