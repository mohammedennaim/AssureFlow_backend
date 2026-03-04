package com.pfe.notification.application.dto;

import com.pfe.notification.domain.model.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateDto {

    private UUID id;
    private UUID notificationId;
    private String name;
    private String bodyTemplate;
    private NotificationStatus status;
}
