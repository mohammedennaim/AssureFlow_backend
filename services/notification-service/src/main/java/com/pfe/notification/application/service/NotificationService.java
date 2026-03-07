package com.pfe.notification.application.service;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.dto.NotificationDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    NotificationDto createNotification(CreateNotificationRequest request);

    NotificationDto getNotificationById(UUID id);

    List<NotificationDto> getNotificationsByPolicyId(UUID policyId);

    List<NotificationDto> getNotificationsByRecipient(String recipient);

    List<NotificationDto> getAllNotifications();

    Page<NotificationDto> getAllNotificationsPaged(int page, int size);

    void sendNotification(UUID id);

    void deleteNotification(UUID id);
}
