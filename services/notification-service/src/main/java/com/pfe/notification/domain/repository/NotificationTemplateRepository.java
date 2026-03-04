package com.pfe.notification.domain.repository;

import com.pfe.notification.domain.model.NotificationTemplate;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface NotificationTemplateRepository {

    NotificationTemplate save(NotificationTemplate template);

    Optional<NotificationTemplate> findById(UUID id);

    Optional<NotificationTemplate> findByName(String name);

    List<NotificationTemplate> findAll();

    void deleteById(UUID id);
}
