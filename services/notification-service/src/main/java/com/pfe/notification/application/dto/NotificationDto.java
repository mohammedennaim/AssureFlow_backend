package com.pfe.notification.application.dto;

import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationStatus;
import com.pfe.notification.domain.model.NotificationType;
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
public class NotificationDto {

    private UUID id;
    private NotificationType type;
    private NotificationChannel channel;
    private String recipient;
    private String linkName;
    private String subject;
    private String content;
    private NotificationStatus status;
    private UUID policyId;
    private LocalDateTime sentAt;
    private boolean read;
}
