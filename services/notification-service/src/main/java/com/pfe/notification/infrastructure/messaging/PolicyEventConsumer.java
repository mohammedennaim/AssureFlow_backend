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

/**
 * Consumer for policy-related Kafka events.
 * Sends SMS + Email notifications for policy lifecycle events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "policy-events", groupId = "notification-service-policy-group", containerFactory = "kafkaListenerContainerFactory")
    public void onPolicyEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA] notification-service received policy event type={} partition={} offset={}",
                eventType, partition, offset);

        if (payload == null || payload.isEmpty()) {
            log.warn("[KAFKA] Received null or empty payload for event type={}, skipping", eventType);
            return;
        }

        switch (eventType) {
            case "policy.created" -> handlePolicyCreated(payload);
            case "policy.approved" -> handlePolicyApproved(payload);
            case "policy.rejected" -> handlePolicyRejected(payload);
            case "policy.renewed" -> handlePolicyRenewed(payload);
            case "policy.cancelled" -> handlePolicyCancelled(payload);
            case "policy.expiring" -> handlePolicyExpiring(payload);
            default -> log.debug("[NOTIFICATION] Ignoring unhandled policy event: {}", eventType);
        }
    }

    private void handlePolicyCreated(Map<String, Object> payload) {
        String policyId = (String) payload.get("policyId");
        String policyNumber = (String) payload.get("policyNumber");
        String clientId = (String) payload.get("clientId");
        String policyType = (String) payload.get("type");
        String clientEmail = (String) payload.get("clientEmail");
        String clientPhone = (String) payload.get("clientPhone");
        Object premiumAmount = payload.get("premiumAmount");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_CREATED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre police d'assurance a été créée")
                .content("Votre police " + policyNumber + " de type " + policyType + 
                        " a été créée avec succès. Prime: " + premiumAmount + "€.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_CREATED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Police " + policyNumber + " créée. Type: " + policyType + 
                            ". Prime: " + premiumAmount + "€.")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] POLICY_CREATED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] POLICY_CREATED email sent to {}", recipient);
    }

    private void handlePolicyApproved(Map<String, Object> payload) {
        String policyId = (String) payload.get("policyId");
        String policyNumber = (String) payload.get("policyNumber");
        String clientId = (String) payload.get("clientId");
        String clientEmail = (String) payload.get("clientEmail");
        String clientPhone = (String) payload.get("clientPhone");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_APPROVED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre police d'assurance a été approuvée")
                .content("Bonne nouvelle ! Votre police " + policyNumber + " a été approuvée. " +
                        "Votre couverture est maintenant active.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_APPROVED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Police " + policyNumber + " approuvée. Couverture active.")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] POLICY_APPROVED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] POLICY_APPROVED email sent to {}", recipient);
    }

    private void handlePolicyRejected(Map<String, Object> payload) {
        String policyId = (String) payload.get("policyId");
        String policyNumber = (String) payload.get("policyNumber");
        String clientId = (String) payload.get("clientId");
        String rejectionReason = (String) payload.get("rejectionReason");
        String clientEmail = (String) payload.get("clientEmail");
        String clientPhone = (String) payload.get("clientPhone");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_REJECTED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre police d'assurance a été refusée")
                .content("Votre demande de police " + policyNumber + " n'a pas pu être approuvée. " +
                        "Raison : " + rejectionReason + ". Contactez-nous pour plus d'informations.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_REJECTED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Police " + policyNumber + " refusée. Raison: " + rejectionReason + 
                            ". Contactez-nous.")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] POLICY_REJECTED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] POLICY_REJECTED email sent to {}", recipient);
    }

    private void handlePolicyRenewed(Map<String, Object> payload) {
        String policyId = (String) payload.get("policyId");
        String policyNumber = (String) payload.get("policyNumber");
        String clientId = (String) payload.get("clientId");
        String clientEmail = (String) payload.get("clientEmail");
        String clientPhone = (String) payload.get("clientPhone");
        Object renewalDate = payload.get("renewalDate");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_RENEWED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre police d'assurance a été renouvelée")
                .content("Votre police " + policyNumber + " a été renouvelée avec succès. " +
                        "Date de renouvellement : " + renewalDate + ".")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_RENEWED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Police " + policyNumber + " renouvelée. Date: " + renewalDate + ".")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] POLICY_RENEWED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] POLICY_RENEWED email sent to {}", recipient);
    }

    private void handlePolicyCancelled(Map<String, Object> payload) {
        String policyId = (String) payload.get("policyId");
        String policyNumber = (String) payload.get("policyNumber");
        String clientId = (String) payload.get("clientId");
        String cancellationReason = (String) payload.get("cancellationReason");
        String clientEmail = (String) payload.get("clientEmail");
        String clientPhone = (String) payload.get("clientPhone");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_CANCELLED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Votre police d'assurance a été annulée")
                .content("Votre police " + policyNumber + " a été annulée. " +
                        "Raison : " + cancellationReason + ". Pour toute question, contactez notre service client.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_CANCELLED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Police " + policyNumber + " annulée. Raison: " + cancellationReason + 
                            ". Contactez-nous.")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] POLICY_CANCELLED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] POLICY_CANCELLED email sent to {}", recipient);
    }

    private void handlePolicyExpiring(Map<String, Object> payload) {
        String policyId = (String) payload.get("policyId");
        String policyNumber = (String) payload.get("policyNumber");
        String clientId = (String) payload.get("clientId");
        String clientEmail = (String) payload.get("clientEmail");
        String clientPhone = (String) payload.get("clientPhone");
        Object expirationDate = payload.get("expirationDate");
        Object daysUntilExpiry = payload.get("daysUntilExpiry");
        
        String recipient = clientEmail != null ? clientEmail : clientId;
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.POLICY_EXPIRING)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Rappel : Votre police d'assurance arrive à expiration")
                .content("Votre police " + policyNumber + " arrivera à expiration le " + expirationDate + 
                        " (dans " + daysUntilExpiry + " jours). Pensez à la renouveler pour rester couvert.")
                .build();
        var emailDto = notificationService.createNotification(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.POLICY_EXPIRING)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Rappel: Police " + policyNumber + " expire le " + expirationDate + 
                            " (dans " + daysUntilExpiry + " jours). Renouvelez maintenant.")
                    .build();
            var smsDto = notificationService.createNotification(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] POLICY_EXPIRING SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] POLICY_EXPIRING email sent to {}", recipient);
    }
}
