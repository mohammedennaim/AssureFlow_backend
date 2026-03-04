package com.pfe.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {

    private UUID id;
    private UUID notificationId;
    private String name;
    private String bodyTemplate;
    private NotificationStatus status;
}
