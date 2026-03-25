package com.pfe.notification.infrastructure.messaging;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.service.impl.NotificationServiceImpl;
import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer for client-related Kafka events.
 * Sends SMS + Email notifications for client lifecycle events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventConsumer {

    private final NotificationServiceImpl notificationService;

    @KafkaListener(topics = "client-events", groupId = "notification-service-client-group-v2", containerFactory = "kafkaListenerContainerFactory")
    public void onClientEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA] notification-service received client event type={} partition={} offset={}",
                eventType, partition, offset);

        if (payload == null || payload.isEmpty()) {
            log.warn("[KAFKA] Received null or empty payload for event type={}, skipping", eventType);
            return;
        }

        switch (eventType) {
            case "client.created" -> handleClientCreated(payload);
            case "client.updated" -> handleClientUpdated(payload);
            case "client.deleted" -> handleClientDeleted(payload);
            default -> log.debug("[NOTIFICATION] Ignoring unhandled client event: {}", eventType);
        }
    }

    private void handleClientCreated(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String clientEmail = (String) payload.get("email");
        String clientPhone = (String) payload.get("phone");
        String firstName = (String) payload.get("firstName");
        String lastName = (String) payload.get("lastName");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_CREATED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Bienvenue chez AssureFlow")
                .content("Bienvenue " + firstName + " " + lastName + " ! Votre compte client a été créé avec succès. " +
                        "Nous sommes ravis de vous compter parmi nous.")
                .build();
        var emailDto = notificationService.createNotificationInternal(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_CREATED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Bienvenue " + firstName + " ! Votre compte AssureFlow est créé.")
                    .build();
            var smsDto = notificationService.createNotificationInternal(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] CLIENT_CREATED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] CLIENT_CREATED email sent to {}", recipient);
    }

    private void handleClientUpdated(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String clientEmail = (String) payload.get("email");
        String clientPhone = (String) payload.get("phone");
        String updatedFields = (String) payload.get("updatedFields");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_RENEWED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Vos informations ont été mises à jour")
                .content("Vos informations client ont été mises à jour avec succès. " +
                        "Champs modifiés : " + updatedFields + ".")
                .build();
        var emailDto = notificationService.createNotificationInternal(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_RENEWED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Vos informations ont été mises à jour.")
                    .build();
            var smsDto = notificationService.createNotificationInternal(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] CLIENT_UPDATED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] CLIENT_UPDATED email sent to {}", recipient);
    }

    private void handleClientDeleted(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String clientEmail = (String) payload.get("email");
        String deletionReason = (String) payload.get("deletionReason");
        String clientPhone = (String) payload.get("phone");

        // Email notification to admin
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_CANCELLED)
                .channel(NotificationChannel.EMAIL)
                .recipient("admin@assureflow.com")
                .subject("Suppression de compte client")
                .content("Le compte client " + clientId + " a été supprimé. Raison : " + deletionReason + ".")
                .build();
        var emailDto = notificationService.createNotificationInternal(emailRequest);
        notificationService.sendNotification(emailDto.getId());

        // SMS notification to admin if phone is available
        String adminPhone = "+1234567890"; // Admin phone for critical alerts
        CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_CANCELLED)
                .channel(NotificationChannel.SMS)
                .recipient(adminPhone)
                .content("ALERTE: Compte client " + clientId + " supprimé. Raison: " + deletionReason + ".")
                .build();
        var smsDto = notificationService.createNotificationInternal(smsRequest);
        notificationService.sendNotification(smsDto.getId());

        // Also send SMS to client if phone is available (for transparency)
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest clientSmsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_CANCELLED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Votre compte AssureFlow a été supprimé. Raison: " + deletionReason + ". Pour toute question, contactez le support.")
                    .build();
            var clientSmsDto = notificationService.createNotificationInternal(clientSmsRequest);
            notificationService.sendNotification(clientSmsDto.getId());
            log.info("[NOTIFICATION] CLIENT_DELETED SMS sent to client {}", clientPhone);
        }

        log.info("[NOTIFICATION] CLIENT_DELETED email sent to admin@assureflow.com");
        log.info("[NOTIFICATION] CLIENT_DELETED SMS sent to admin {}", adminPhone);
    }
}
