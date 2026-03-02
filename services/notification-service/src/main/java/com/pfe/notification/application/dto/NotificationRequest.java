package com.pfe.notification.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotBlank(message = "Recipient ID is required")
    private String recipientId;

    @NotNull(message = "Notification type is required")
    private String type;

    private String subject;

    @NotBlank(message = "Message is required")
    private String message;
}
