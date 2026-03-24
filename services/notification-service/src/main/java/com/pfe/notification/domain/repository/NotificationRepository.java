package com.pfe.notification.domain.repository;

import com.pfe.notification.domain.model.Notification;
import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationStatus;
import com.pfe.notification.domain.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(UUID id);

    List<Notification> findByPolicyId(UUID policyId);

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findAll();

    Page<Notification> findAllPaged(Pageable pageable);

    long countByRecipientAndReadFalse(String recipient);

    List<Notification> saveAll(List<Notification> notifications);

    void deleteById(UUID id);

    // Statistics methods
    long count();

    long countByStatus(NotificationStatus status);

    long countByChannel(NotificationChannel channel);

    long countByCreatedAtAfter(LocalDateTime date);
}
