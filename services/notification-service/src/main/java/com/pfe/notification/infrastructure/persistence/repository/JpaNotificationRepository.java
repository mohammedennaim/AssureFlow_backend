package com.pfe.notification.infrastructure.persistence.repository;

import com.pfe.notification.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, String> {
    List<NotificationEntity> findByRecipientId(String recipientId);

    List<NotificationEntity> findByRecipientIdAndReadFalse(String recipientId);
}
