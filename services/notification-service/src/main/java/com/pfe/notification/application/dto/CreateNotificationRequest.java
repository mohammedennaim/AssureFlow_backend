package com.pfe.notification.application.dto;

import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    @NotNull
    private NotificationType type;

    @NotNull
    private NotificationChannel channel;

    @NotBlank
    private String recipient;

    private String linkName;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    private UUID policyId;
}
