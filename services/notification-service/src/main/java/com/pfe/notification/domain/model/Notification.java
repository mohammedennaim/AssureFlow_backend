package com.pfe.notification.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String id;
    private String recipientId;
    private NotificationType type;
    private String subject;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }
}
