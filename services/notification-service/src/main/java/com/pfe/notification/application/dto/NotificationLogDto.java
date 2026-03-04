package com.pfe.notification.application.dto;

import com.pfe.notification.domain.model.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogDto {

    private UUID id;
    private UUID notificationId;
    private LocalDateTime timestamp;
    private NotificationStatus status;
}
