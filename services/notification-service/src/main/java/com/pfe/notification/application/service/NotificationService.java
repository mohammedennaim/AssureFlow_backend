package com.pfe.notification.application.service;

import com.pfe.notification.application.dto.NotificationRequest;
import com.pfe.notification.application.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse sendNotification(NotificationRequest request);

    NotificationResponse getNotificationById(String id);

    List<NotificationResponse> getNotificationsByRecipient(String recipientId);

    List<NotificationResponse> getUnreadNotifications(String recipientId);

    List<NotificationResponse> getAllNotifications();

    NotificationResponse markAsRead(String id);

    void deleteNotification(String id);
}
