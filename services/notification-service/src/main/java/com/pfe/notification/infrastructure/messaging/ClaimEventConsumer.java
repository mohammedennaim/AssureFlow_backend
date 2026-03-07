package com.pfe.notification.infrastructure.messaging;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.service.NotificationService;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "claim-events", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void onClaimEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA] notification-service received claim event type={} partition={} offset={}",
                eventType, partition, offset);

        switch (eventType) {
            case "claim.submitted" -> handleClaimSubmitted(payload);
            case "claim.approved" -> handleClaimApproved(payload);
            case "claim.rejected" -> handleClaimRejected(payload);
            case "claim.paid" -> handleClaimPaid(payload);
            default -> log.debug("[NOTIFICATION] Ignoring unhandled claim event: {}", eventType);
        }
    }

    private void handleClaimSubmitted(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .type(NotificationType.CLAIM_SUBMITTED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre réclamation a été soumise")
                .content("Votre réclamation " + claimId + " a bien été enregistrée. Nous revenons vers vous sous 48h.")
                .build();
        var dto = notificationService.createNotification(request);
        notificationService.sendNotification(dto.getId());
        log.info("[NOTIFICATION] CLAIM_SUBMITTED notification sent to {}", recipient);
    }

    private void handleClaimApproved(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        Object amount   = payload.get("approvedAmount");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .type(NotificationType.CLAIM_APPROVED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre réclamation est approuvée")
                .content("Félicitations ! Votre réclamation " + claimId + " est approuvée. Montant : " + amount + ".")
                .build();
        var dto = notificationService.createNotification(request);
        notificationService.sendNotification(dto.getId());
        log.info("[NOTIFICATION] CLAIM_APPROVED notification sent to {}", recipient);
    }

    private void handleClaimRejected(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .type(NotificationType.CLAIM_SUBMITTED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre réclamation a été refusée")
                .content("Nous sommes désolés. Votre réclamation " + claimId + " n'a pas pu être approuvée.")
                .build();
        var dto = notificationService.createNotification(request);
        notificationService.sendNotification(dto.getId());
        log.info("[NOTIFICATION] CLAIM_REJECTED notification sent to {}", recipient);
    }

    private void handleClaimPaid(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        Object amount   = payload.get("paidAmount");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .type(NotificationType.PAYMENT_RECEIVED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Paiement effectué pour votre réclamation")
                .content("Le règlement de " + amount + " pour la réclamation " + claimId + " a été effectué.")
                .build();
        var dto = notificationService.createNotification(request);
        notificationService.sendNotification(dto.getId());
        log.info("[NOTIFICATION] CLAIM_PAID notification sent to {}", recipient);
    }
}
