package com.pfe.notification.domain.repository;

import com.pfe.notification.domain.model.NotificationLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationLogRepository {

    NotificationLog save(NotificationLog log);

    Optional<NotificationLog> findById(UUID id);

    List<NotificationLog> findByNotificationId(UUID notificationId);

    List<NotificationLog> findAll();

    void deleteById(UUID id);
}
