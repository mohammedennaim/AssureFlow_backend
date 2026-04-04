package com.pfe.notification.domain.model;

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
public class Notification {

    private UUID id;
    private NotificationType type;
    private NotificationChannel channel;
    private String recipient;
    private String linkName;
    private String subject;
    private String content;
    private NotificationStatus status;
    private UUID policyId;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private boolean read;

    public void send() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }
    
    public void fail() {
        this.status = NotificationStatus.FAILED;
    }
    
    public void markAsRead() {
        this.read = true;
    }
}
