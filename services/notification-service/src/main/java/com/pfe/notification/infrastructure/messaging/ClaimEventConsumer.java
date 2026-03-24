package com.pfe.notification.infrastructure.messaging;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.service.NotificationService;
import com.pfe.notification.domain.model.NotificationChannel;
import com.pfe.notification.domain.model.NotificationType;
import com.pfe.notification.infrastructure.sms.TwilioSmsService;
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
    private final TwilioSmsService twilioSmsService;

    @KafkaListener(topics = "claim-events", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void onClaimEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA] notification-service received claim event type={} partition={} offset={}",
                eventType, partition, offset);

        if (payload == null || payload.isEmpty()) {
            log.warn("[KAFKA] Received null or empty payload for event type={}, skipping", eventType);
            return;
        }

        switch (eventType) {
            case "claim.submitted" -> handleClaimSubmitted(payload);
            case "claim.approved" -> handleClaimApproved(payload);
            case "claim.rejected" -> handleClaimRejected(payload);
            case "claim.paid" -> handleClaimPaid(payload);
            case "claim.sla.breached" -> handleClaimSlaBreached(payload);
            default -> log.debug("[NOTIFICATION] Ignoring unhandled claim event: {}", eventType);
        }
    }

    private void handleClaimSubmitted(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String clientPhone = (String) payload.get("clientPhone");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.CLAIM_SUBMITTED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre réclamation a été soumise")
                .content("Votre réclamation " + claimId + " a bien été enregistrée. Nous revenons vers vous sous 48h.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.CLAIM_SUBMITTED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Votre réclamation " + claimId + " a été enregistrée. Nous vous contactons sous 48h.")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] CLAIM_SUBMITTED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] CLAIM_SUBMITTED email sent to {}", recipient);
    }

    private void handleClaimApproved(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        Object amount   = payload.get("approvedAmount");
        String clientPhone = (String) payload.get("clientPhone");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.CLAIM_APPROVED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre réclamation est approuvée")
                .content("Félicitations ! Votre réclamation " + claimId + " est approuvée. Montant : " + amount + ".")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.CLAIM_APPROVED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Bonne nouvelle ! Votre réclamation " + claimId + " est approuvée. Montant: " + amount + ".")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] CLAIM_APPROVED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] CLAIM_APPROVED email sent to {}", recipient);
    }

    private void handleClaimRejected(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String clientPhone = (String) payload.get("clientPhone");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.CLAIM_REJECTED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre réclamation a été refusée")
                .content("Nous sommes désolés. Votre réclamation " + claimId + " n'a pas pu être approuvée.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.CLAIM_REJECTED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Votre réclamation " + claimId + " n'a pas été approuvée. Contactez-nous pour plus d'infos.")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] CLAIM_REJECTED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] CLAIM_REJECTED email sent to {}", recipient);
    }

    private void handleClaimPaid(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        Object amount   = payload.get("paidAmount");
        String clientPhone = (String) payload.get("clientPhone");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.CLAIM_PAID)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Paiement effectué pour votre réclamation")
                .content("Le règlement de " + amount + " pour la réclamation " + claimId + " a été effectué.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.CLAIM_PAID)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Paiement de " + amount + " effectué pour réclamation " + claimId + ".")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] CLAIM_PAID SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] CLAIM_PAID email sent to {}", recipient);
    }

    private void handleClaimSlaBreached(Map<String, Object> payload) {
        String claimId = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        String clientId = (String) payload.get("clientId");
        String clientEmail = (String) payload.get("clientEmail");
        String clientPhone = (String) payload.get("clientPhone");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.CLAIM_UNDER_REVIEW)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Mise à jour de votre réclamation")
                .content("Votre réclamation " + claimNumber + " est en cours de traitement avancé. " +
                        "Un responsable vous contactera dans les plus brefs délais.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.CLAIM_UNDER_REVIEW)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Réclamation " + claimNumber + " en cours de traitement avancé. Un responsable vous contactera.")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] CLAIM_SLA_BREACHED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] CLAIM_SLA_BREACHED email sent to {}", recipient);
    }
}
