package com.pfe.notification.application.service;

import com.pfe.notification.application.dto.NotificationLogDto;

import java.util.List;
import java.util.UUID;

public interface NotificationLogService {

    NotificationLogDto getLogById(UUID id);

    List<NotificationLogDto> getLogsByNotificationId(UUID notificationId);

    List<NotificationLogDto> getAllLogs();
}
