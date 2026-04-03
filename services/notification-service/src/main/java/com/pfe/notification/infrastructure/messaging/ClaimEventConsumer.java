package com.pfe.notification.infrastructure.messaging;

import com.pfe.notification.application.dto.CreateNotificationRequest;
import com.pfe.notification.application.service.impl.NotificationServiceImpl;
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

/**
 * Consumer for claim-related Kafka events.
 * Sends notifications to CLIENTS only (email, SMS, in-app).
 * Admin/Agent/Finance do NOT receive notifications.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimEventConsumer {

    private final NotificationServiceImpl notificationService;
    private final TwilioSmsService twilioSmsService;

    @KafkaListener(topics = "claim-events", groupId = "notification-service-claim-group-v2", containerFactory = "kafkaListenerContainerFactory")
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
            case "claim.under_review" -> handleClaimUnderReview(payload);
            case "claim.approved" -> handleClaimApproved(payload);
            case "claim.rejected" -> handleClaimRejected(payload);
            case "claim.paid" -> handleClaimPaid(payload);
            case "claim.info_requested" -> handleClaimInfoRequested(payload);
            case "claim.closed" -> handleClaimClosed(payload);
            case "claim.updated" -> handleClaimUpdated(payload);
            case "claim.sla.breached" -> handleClaimSlaBreached(payload);
            default -> log.debug("[NOTIFICATION] Ignoring unhandled claim event: {}", eventType);
        }
    }

    /**
     * Handles claim.submitted event - notifies CLIENT and ADMIN
     */
    private void handleClaimSubmitted(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        String clientPhone = (String) payload.get("clientPhone");
        String clientEmail = (String) payload.get("clientEmail");
        String recipient = clientEmail != null ? clientEmail : clientId;

        // Send notification to CLIENT (EMAIL + SMS)
        if (recipient == null || recipient.isBlank()) {
            log.warn("[NOTIFICATION] No recipient (email or clientId) for claim={}, skipping email notification", claimId);
            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_SUBMITTED,
                    "Réclamation " + claimNumber + " enregistrée",
                    "Votre réclamation " + claimNumber + " a été enregistrée. Nous vous contactons sous 48h.");
            }
        } else {
            sendEmailNotification(recipient, NotificationType.CLAIM_SUBMITTED,
                "Votre réclamation a été soumise",
                "Votre réclamation " + claimNumber + " a bien été enregistrée. Nous revenons vers vous sous 48h.");

            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_SUBMITTED,
                    "Réclamation " + claimNumber + " enregistrée",
                    "Votre réclamation " + claimNumber + " a été enregistrée. Nous vous contactons sous 48h.");
            }

            log.info("[NOTIFICATION] CLAIM_SUBMITTED email sent to client {}", recipient);
        }

        // Send IN_APP notification to ADMIN
        sendInAppNotificationToAdmin("CLAIM_SUBMITTED",
            "Nouvelle réclamation soumise",
            "Le client " + (clientEmail != null ? clientEmail : clientId) + " a soumis la réclamation " + claimNumber);
    }

    /**
     * Handles claim.under_review event - notifies CLIENT only
     */
    private void handleClaimUnderReview(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        String clientPhone = (String) payload.get("clientPhone");
        String clientEmail = (String) payload.get("clientEmail");
        String recipient = clientEmail != null ? clientEmail : clientId;

        if (recipient == null || recipient.isBlank()) {
            log.warn("[NOTIFICATION] No recipient (email or clientId) for claim={}, skipping email notification", claimId);
            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_UNDER_REVIEW,
                    "Réclamation " + claimNumber + " en cours de traitement",
                    "Votre réclamation " + claimNumber + " est en cours de traitement. Nous vous contactons sous 48h.");
            }
            return;
        }

        sendEmailNotification(recipient, NotificationType.CLAIM_UNDER_REVIEW,
            "Votre réclamation " + claimNumber + " est en cours de traitement",
            "Bonjour,\n\nVotre réclamation " + claimNumber + " est en cours de traitement par nos équipes.\n\nNous reviendrons vers vous sous 48h.\n\nCordialement,\nAssureFlow");

        if (clientPhone != null && !clientPhone.isBlank()) {
            sendSmsNotification(clientPhone, NotificationType.CLAIM_UNDER_REVIEW,
                "Réclamation " + claimNumber + " en traitement",
                "AssureFlow: Votre réclamation " + claimNumber + " est en cours de traitement. Nous vous contactons sous 48h.");
        }

        log.info("[NOTIFICATION] CLAIM_UNDER_REVIEW email sent to client {}", recipient);

        // Send IN_APP notification to ADMIN
        sendInAppNotificationToAdmin("CLAIM_UNDER_REVIEW",
            "Réclamation en cours de traitement",
            "La réclamation " + claimNumber + " est en cours de traitement");
    }

    /**
     * Handles claim.approved event - notifies CLIENT only
     */
    private void handleClaimApproved(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        Object amount   = payload.get("approvedAmount");
        String clientPhone = (String) payload.get("clientPhone");
        String clientEmail = (String) payload.get("clientEmail");
        String recipient = clientEmail != null ? clientEmail : clientId;
        String claimRef = claimNumber != null && !claimNumber.isBlank() ? claimNumber : claimId;

        if (recipient == null || recipient.isBlank()) {
            log.warn("[NOTIFICATION] No recipient (email or clientId) for claim={}, skipping email notification", claimId);
            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_APPROVED,
                    "Réclamation " + claimRef + " approuvée",
                    "Bonne nouvelle ! Votre réclamation " + claimRef + " est approuvée. Montant: " + amount + ".");
            }
            return;
        }

        sendEmailNotification(recipient, NotificationType.CLAIM_APPROVED,
            "Votre réclamation est approuvée",
            "Félicitations ! Votre réclamation " + claimRef + " est approuvée. Montant : " + amount + ".");

        if (clientPhone != null && !clientPhone.isBlank()) {
            sendSmsNotification(clientPhone, NotificationType.CLAIM_APPROVED,
                "Réclamation " + claimRef + " approuvée",
                "Bonne nouvelle ! Votre réclamation " + claimRef + " est approuvée. Montant: " + amount + ".");
        }

        log.info("[NOTIFICATION] CLAIM_APPROVED email sent to {}", recipient);

        // Send IN_APP notification to ADMIN
        sendInAppNotificationToAdmin("CLAIM_APPROVED",
            "Réclamation approuvée",
            "La réclamation " + claimRef + " a été approuvée. Montant: " + amount);
    }

    /**
     * Handles claim.rejected event - notifies CLIENT and ADMIN
     */
    private void handleClaimRejected(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        String rejectionReason = (String) payload.get("rejectionReason");
        String clientPhone = (String) payload.get("clientPhone");
        String clientEmail = (String) payload.get("clientEmail");
        String recipient = clientEmail != null ? clientEmail : clientId;
        String claimRef = claimNumber != null && !claimNumber.isBlank() ? claimNumber : claimId;
        String reasonSuffix = (rejectionReason != null && !rejectionReason.isBlank())
            ? " Raison: " + rejectionReason + "."
            : "";

        if (recipient == null || recipient.isBlank()) {
            log.warn("[NOTIFICATION] No recipient (email or clientId) for claim={}, skipping email notification", claimId);
            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_REJECTED,
                    "Réclamation " + claimRef + " refusée",
                    "Votre réclamation " + claimRef + " n'a pas été approuvée." + reasonSuffix);
            }
            return;
        }

        sendEmailNotification(recipient, NotificationType.CLAIM_REJECTED,
            "Votre réclamation a été refusée",
            "Nous sommes désolés. Votre réclamation " + claimRef + " n'a pas pu être approuvée." + reasonSuffix);

        if (clientPhone != null && !clientPhone.isBlank()) {
            sendSmsNotification(clientPhone, NotificationType.CLAIM_REJECTED,
                "Réclamation " + claimRef + " refusée",
                "Votre réclamation " + claimRef + " n'a pas été approuvée." + reasonSuffix);
        }

        log.info("[NOTIFICATION] CLAIM_REJECTED email sent to {}", recipient);

        // Send IN_APP notification to ADMIN
        sendInAppNotificationToAdmin("CLAIM_REJECTED",
            "Réclamation refusée",
            "La réclamation " + claimRef + " a été refusée." + (rejectionReason != null ? " Raison: " + rejectionReason : ""));
    }

    /**
     * Handles claim.paid event - notifies CLIENT only
     */
    private void handleClaimPaid(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId  = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        Object amount   = payload.get("paidAmount");
        String clientPhone = (String) payload.get("clientPhone");
        String clientEmail = (String) payload.get("clientEmail");
        String recipient = clientEmail != null ? clientEmail : clientId;
        String claimRef = claimNumber != null && !claimNumber.isBlank() ? claimNumber : claimId;

        if (recipient == null || recipient.isBlank()) {
            log.warn("[NOTIFICATION] No recipient (email or clientId) for claim={}, skipping email notification", claimId);
            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_PAID,
                    "Paiement réclamation " + claimRef,
                    "Paiement de " + amount + " effectué pour réclamation " + claimRef + ".");
            }
            return;
        }

        sendEmailNotification(recipient, NotificationType.CLAIM_PAID,
            "Paiement effectué pour votre réclamation",
            "Le règlement de " + amount + " pour la réclamation " + claimRef + " a été effectué.");

        if (clientPhone != null && !clientPhone.isBlank()) {
            sendSmsNotification(clientPhone, NotificationType.CLAIM_PAID,
                "Paiement réclamation " + claimRef,
                "Paiement de " + amount + " effectué pour réclamation " + claimRef + ".");
        }

        log.info("[NOTIFICATION] CLAIM_PAID email sent to {}", recipient);

        // Send IN_APP notification to ADMIN
        sendInAppNotificationToAdmin("CLAIM_PAID",
            "Réclamation payée",
            "Le paiement de " + amount + " a été effectué pour la réclamation " + claimRef);
    }
    private void handleClaimSlaBreached(Map<String, Object> payload) {
        String claimId = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        String clientId = (String) payload.get("clientId");
        String clientEmail = (String) payload.get("clientEmail");
        String clientPhone = (String) payload.get("clientPhone");

        String recipient = clientEmail != null ? clientEmail : clientId;

        if (recipient == null || recipient.isBlank()) {
            log.warn("[NOTIFICATION] No recipient (email or clientId) for claim={}, skipping email notification", claimNumber);
            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_UNDER_REVIEW,
                    "Réclamation " + claimNumber + " en traitement",
                    "Réclamation " + claimNumber + " en cours de traitement avancé. Un responsable vous contactera.");
            }
            return;
        }

        sendEmailNotification(recipient, NotificationType.CLAIM_UNDER_REVIEW,
            "Mise à jour de votre réclamation",
            "Votre réclamation " + claimNumber + " est en cours de traitement avancé. " +
            "Un responsable vous contactera dans les plus brefs délais.");

        if (clientPhone != null && !clientPhone.isBlank()) {
            sendSmsNotification(clientPhone, NotificationType.CLAIM_UNDER_REVIEW,
                "Réclamation " + claimNumber + " en traitement",
                "Réclamation " + claimNumber + " en cours de traitement avancé. Un responsable vous contactera.");
        }

        log.info("[NOTIFICATION] CLAIM_SLA_BREACHED email sent to {}", recipient);
    }

    /**
     * Handles claim.info_requested event - notifies CLIENT only
     */
    private void handleClaimInfoRequested(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        String clientPhone = (String) payload.get("clientPhone");
        String clientEmail = (String) payload.get("clientEmail");
        String recipient = clientEmail != null ? clientEmail : clientId;
        String claimRef = claimNumber != null && !claimNumber.isBlank() ? claimNumber : claimId;

        if (recipient == null || recipient.isBlank()) {
            log.warn("[NOTIFICATION] No recipient (email or clientId) for claim={}, skipping email notification", claimRef);
            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_UNDER_REVIEW,
                    "Infos requises - Réclamation " + claimRef,
                    "Des informations complémentaires sont requises pour votre réclamation " + claimRef + ".");
            }
            return;
        }

        sendEmailNotification(recipient, NotificationType.CLAIM_UNDER_REVIEW,
            "Informations complémentaires requises",
            "Des informations complémentaires sont requises pour votre réclamation " + claimRef + ". Merci de compléter votre dossier.");

        if (clientPhone != null && !clientPhone.isBlank()) {
            sendSmsNotification(clientPhone, NotificationType.CLAIM_UNDER_REVIEW,
                "Infos requises - Réclamation " + claimRef,
                "AssureFlow: Merci d'ajouter les informations manquantes pour la réclamation " + claimRef + ".");
        }

        log.info("[NOTIFICATION] CLAIM_INFO_REQUESTED notification sent to {}", recipient);
    }

    /**
     * Handles claim.closed event - notifies CLIENT only
     */
    private void handleClaimClosed(Map<String, Object> payload) {
        String clientId = (String) payload.get("clientId");
        String claimId = (String) payload.get("claimId");
        String claimNumber = (String) payload.get("claimNumber");
        String clientPhone = (String) payload.get("clientPhone");
        String clientEmail = (String) payload.get("clientEmail");
        String recipient = clientEmail != null ? clientEmail : clientId;
        String claimRef = claimNumber != null && !claimNumber.isBlank() ? claimNumber : claimId;

        if (recipient == null || recipient.isBlank()) {
            log.warn("[NOTIFICATION] No recipient (email or clientId) for claim={}, skipping email notification", claimRef);
            if (clientPhone != null && !clientPhone.isBlank()) {
                sendSmsNotification(clientPhone, NotificationType.CLAIM_UNDER_REVIEW,
                    "Réclamation " + claimRef + " clôturée",
                    "Votre réclamation " + claimRef + " est maintenant clôturée.");
            }
            return;
        }

        sendEmailNotification(recipient, NotificationType.CLAIM_UNDER_REVIEW,
            "Votre réclamation est clôturée",
            "Votre réclamation " + claimRef + " a été clôturée. Merci de votre confiance.");

        if (clientPhone != null && !clientPhone.isBlank()) {
            sendSmsNotification(clientPhone, NotificationType.CLAIM_UNDER_REVIEW,
                "Réclamation " + claimRef + " clôturée",
                "AssureFlow: Votre réclamation " + claimRef + " est clôturée.");
        }

        log.info("[NOTIFICATION] CLAIM_CLOSED notification sent to {}", recipient);
    }

    /**
     * Backward-compatible handler for generic claim.updated events.
     * Dispatches to the proper specific handler when newStatus is provided.
     */
    private void handleClaimUpdated(Map<String, Object> payload) {
        String newStatus = payload.get("newStatus") != null ? payload.get("newStatus").toString() : null;
        if (newStatus == null || newStatus.isBlank()) {
            log.debug("[NOTIFICATION] claim.updated without newStatus, skipping payload={}", payload);
            return;
        }

        switch (newStatus) {
            case "UNDER_REVIEW" -> handleClaimUnderReview(payload);
            case "APPROVED" -> handleClaimApproved(payload);
            case "REJECTED" -> handleClaimRejected(payload);
            case "PAID" -> handleClaimPaid(payload);
            case "INFO_REQUESTED" -> handleClaimInfoRequested(payload);
            case "CLOSED" -> handleClaimClosed(payload);
            default -> log.debug("[NOTIFICATION] claim.updated with unsupported newStatus={}, payload={}", newStatus, payload);
        }
    }

    // ============ Helper methods ============

    private void sendEmailNotification(String recipient, NotificationType type, String subject, String content) {
        try {
            CreateNotificationRequest request = CreateNotificationRequest.builder()
                    .type(type)
                    .channel(NotificationChannel.EMAIL)
                    .recipient(recipient)
                    .subject(subject)
                    .content(content)
                    .build();
            var dto = notificationService.createNotificationInternal(request);
            notificationService.sendNotificationInternal(dto.getId());
        } catch (Exception e) {
            log.error("[NOTIFICATION] Failed to send email to {}: {}", recipient, e.getMessage());
        }
    }

    private void sendSmsNotification(String phone, NotificationType type, String subject, String content) {
        try {
            CreateNotificationRequest request = CreateNotificationRequest.builder()
                    .type(type)
                    .channel(NotificationChannel.SMS)
                    .recipient(phone)
                    .subject(subject)
                    .content(content)
                    .build();
            var dto = notificationService.createNotificationInternal(request);
            notificationService.sendNotificationInternal(dto.getId());
        } catch (Exception e) {
            log.error("[NOTIFICATION] Failed to send SMS to {}: {}", phone, e.getMessage());
        }
    }

    /**
     * Sends IN_APP notification to ADMIN for dashboard visibility
     */
    private void sendInAppNotificationToAdmin(String type, String subject, String content) {
        try {
            NotificationType notificationType;
            try {
                notificationType = NotificationType.valueOf(type);
            } catch (IllegalArgumentException e) {
                notificationType = NotificationType.CLAIM_SUBMITTED;
            }

            CreateNotificationRequest request = CreateNotificationRequest.builder()
                    .type(notificationType)
                    .channel(NotificationChannel.IN_APP)
                    .recipient("ADMIN")
                    .subject(subject)
                    .content(content)
                    .build();
            var dto = notificationService.createNotificationInternal(request);
            log.info("[NOTIFICATION] IN_APP notification created for ADMIN: {}", subject);
        } catch (Exception e) {
            log.error("[NOTIFICATION] Failed to send IN_APP notification to ADMIN: {}", e.getMessage());
        }
    }
}
