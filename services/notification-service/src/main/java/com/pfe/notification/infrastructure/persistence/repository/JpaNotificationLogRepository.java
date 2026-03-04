package com.pfe.notification.infrastructure.persistence.repository;

import com.pfe.notification.infrastructure.persistence.entity.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaNotificationLogRepository extends JpaRepository<NotificationLogEntity, UUID> {

    List<NotificationLogEntity> findByNotificationId(UUID notificationId);
}
