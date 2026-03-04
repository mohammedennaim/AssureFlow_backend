package com.pfe.notification.infrastructure.persistence.repository;

import com.pfe.notification.infrastructure.persistence.entity.NotificationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaNotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, UUID> {

    Optional<NotificationTemplateEntity> findByName(String name);
}
