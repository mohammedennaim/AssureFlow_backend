package com.pfe.notification.domain.repository;

import com.pfe.notification.domain.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> findById(String id);

    List<Notification> findByRecipientId(String recipientId);

    List<Notification> findUnreadByRecipientId(String recipientId);

    List<Notification> findAll();

    void deleteById(String id);
}
