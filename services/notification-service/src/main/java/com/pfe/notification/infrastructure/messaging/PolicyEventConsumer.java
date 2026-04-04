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
 * Consumer for policy-related Kafka events.
 * Sends SMS + Email notifications for policy lifecycle events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyEventConsumer {

    private final NotificationServiceImpl notificationService;

    @KafkaListener(topics = "policy-events", groupId = "notification-service-policy-group-v2", containerFactory = "kafkaListenerContainerFactory")
    public void onPolicyEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info("[KAFKA] notification-service received policy event type={} partition={} offset={}",
                    eventType, partition, offset);

            if (payload == null || payload.isEmpty()) {
                log.warn("[KAFKA] Received null or empty payload for event type={}, skipping", eventType);
                return;
            }

            switch (eventType) {
                case "policy.created"   -> handlePolicyCreated(payload);
                case "policy.approved"  -> handlePolicyApproved(payload);
                case "policy.rejected"  -> handlePolicyRejected(payload);
                case "policy.renewed"   -> handlePolicyRenewed(payload);
                case "policy.cancelled" -> handlePolicyCancelled(payload);
                case "policy.expiring"  -> handlePolicyExpiring(payload);
                default -> log.debug("[NOTIFICATION] Ignoring unhandled policy event: {}", eventType);
            }
        } catch (Exception e) {
            log.error("[KAFKA] Exception in onPolicyEvent for type={}: {}", eventType, e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------

    private void handlePolicyCreated(Map<String, Object> payload) {
        String policyNumber = (String) payload.get("policyNumber");
        String policyType   = (String) payload.get("type");
        String clientEmail  = (String) payload.get("clientEmail");
        String clientPhone  = (String) payload.get("clientPhone");
        Object premiumAmount = payload.get("premiumAmount");

        String emailSubject = "Votre police d'assurance a ete creee";
        String emailContent = "Votre police " + policyNumber + " de type " + policyType
                + " a ete creee avec succes. Prime: " + (premiumAmount != null ? premiumAmount : "N/A") + "EUR.";
        String smsContent   = "Police " + policyNumber + " creee. Type: " + policyType
                + ". Prime: " + (premiumAmount != null ? premiumAmount : "N/A") + "EUR.";
        String smsSubject   = "Police " + policyNumber + " creee";

        sendNotifications(NotificationType.POLICY_CREATED, policyNumber,
                clientEmail, clientPhone, emailSubject, emailContent, smsSubject, smsContent);
    }

    private void handlePolicyApproved(Map<String, Object> payload) {
        String policyNumber = (String) payload.get("policyNumber");
        String clientEmail  = (String) payload.get("clientEmail");
        String clientPhone  = (String) payload.get("clientPhone");

        String emailSubject = "Votre police d'assurance a ete approuvee";
        String emailContent = "Bonne nouvelle ! Votre police " + policyNumber
                + " a ete approuvee. Votre couverture est maintenant active.";
        String smsContent   = "Police " + policyNumber + " approuvee. Couverture active.";
        String smsSubject   = "Police " + policyNumber + " approuvee";

        sendNotifications(NotificationType.POLICY_APPROVED, policyNumber,
                clientEmail, clientPhone, emailSubject, emailContent, smsSubject, smsContent);
    }

    private void handlePolicyRejected(Map<String, Object> payload) {
        String policyNumber     = (String) payload.get("policyNumber");
        String rejectionReason  = (String) payload.get("rejectionReason");
        String clientEmail      = (String) payload.get("clientEmail");
        String clientPhone      = (String) payload.get("clientPhone");

        String emailSubject = "Votre police d'assurance a ete refusee";
        String emailContent = "Votre demande de police " + policyNumber + " n'a pas pu etre approuvee. "
                + "Raison : " + rejectionReason + ". Contactez-nous pour plus d'informations.";
        String smsContent   = "Police " + policyNumber + " refusee. Raison: " + rejectionReason + ". Contactez-nous.";
        String smsSubject   = "Police " + policyNumber + " refusee";

        sendNotifications(NotificationType.POLICY_REJECTED, policyNumber,
                clientEmail, clientPhone, emailSubject, emailContent, smsSubject, smsContent);
    }

    private void handlePolicyRenewed(Map<String, Object> payload) {
        String policyNumber = (String) payload.get("policyNumber");
        String clientEmail  = (String) payload.get("clientEmail");
        String clientPhone  = (String) payload.get("clientPhone");
        Object renewalDate  = payload.get("renewalDate");

        String emailSubject = "Votre police d'assurance a ete renouvelee";
        String emailContent = "Votre police " + policyNumber + " a ete renouvelee avec succes. "
                + "Date de renouvellement : " + renewalDate + ".";
        String smsContent   = "Police " + policyNumber + " renouvelee. Date: " + renewalDate + ".";
        String smsSubject   = "Police " + policyNumber + " renouvelee";

        sendNotifications(NotificationType.POLICY_RENEWED, policyNumber,
                clientEmail, clientPhone, emailSubject, emailContent, smsSubject, smsContent);
    }

    private void handlePolicyCancelled(Map<String, Object> payload) {
        String policyNumber        = (String) payload.get("policyNumber");
        String cancellationReason  = (String) payload.get("cancellationReason");
        String clientEmail         = (String) payload.get("clientEmail");
        String clientPhone         = (String) payload.get("clientPhone");

        String emailSubject = "Votre police d'assurance a ete annulee";
        String emailContent = "Votre police " + policyNumber + " a ete annulee. "
                + "Raison : " + cancellationReason + ". Pour toute question, contactez notre service client.";
        String smsContent   = "Police " + policyNumber + " annulee. Raison: " + cancellationReason + ". Contactez-nous.";
        String smsSubject   = "Police " + policyNumber + " annulee";

        sendNotifications(NotificationType.POLICY_CANCELLED, policyNumber,
                clientEmail, clientPhone, emailSubject, emailContent, smsSubject, smsContent);
    }

    private void handlePolicyExpiring(Map<String, Object> payload) {
        String policyNumber   = (String) payload.get("policyNumber");
        String clientEmail    = (String) payload.get("clientEmail");
        String clientPhone    = (String) payload.get("clientPhone");
        Object expirationDate = payload.get("expirationDate");
        Object daysUntilExpiry = payload.get("daysUntilExpiry");

        String emailSubject = "Rappel Important : Votre police d'assurance " + policyNumber + " arrive a expiration";
        String emailContent = buildExpiringEmailContent(policyNumber, expirationDate, daysUntilExpiry);
        String smsContent   = buildExpiringSmsContent(policyNumber, expirationDate, daysUntilExpiry);
        String smsSubject   = "AssureFlow - " + policyNumber;

        sendNotifications(NotificationType.POLICY_EXPIRING, policyNumber,
                clientEmail, clientPhone, emailSubject, emailContent, smsSubject, smsContent);
    }

    // -------------------------------------------------------------------------
    // Shared send utility — avoids duplicating email+SMS logic in every handler
    // -------------------------------------------------------------------------

    private void sendNotifications(NotificationType type, String policyNumber,
                                   String clientEmail, String clientPhone,
                                   String emailSubject, String emailContent,
                                   String smsSubject, String smsContent) {

        boolean hasEmail = clientEmail != null && !clientEmail.isBlank();
        boolean hasPhone = clientPhone != null && !clientPhone.isBlank();

        if (!hasEmail && !hasPhone) {
            log.warn("[NOTIFICATION] No recipient for policy={}, skipping all notifications", policyNumber);
            return;
        }

        if (hasEmail) {
            send(type, NotificationChannel.EMAIL, clientEmail, emailSubject, emailContent);
            log.info("[NOTIFICATION] {} EMAIL sent to {}", type, clientEmail);
        }

        if (hasPhone) {
            send(type, NotificationChannel.SMS, clientPhone, smsSubject, smsContent);
            log.info("[NOTIFICATION] {} SMS sent to {}", type, clientPhone);
        }
    }

    private void send(NotificationType type, NotificationChannel channel,
                      String recipient, String subject, String content) {
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .type(type)
                .channel(channel)
                .recipient(recipient)
                .subject(subject)
                .content(content)
                .build();
        var dto = notificationService.createNotificationInternal(request);
        notificationService.sendNotificationInternal(dto.getId());
    }

    // -------------------------------------------------------------------------
    // Content builders for expiring notifications
    // -------------------------------------------------------------------------

    private String buildExpiringEmailContent(String policyNumber, Object expirationDate, Object daysUntilExpiry) {
        return "Bonjour,\n\n"
                + "Nous vous informons que votre police d'assurance N " + policyNumber
                + " arrivera a expiration le " + expirationDate
                + " (dans " + daysUntilExpiry + " jours).\n\n"
                + "Afin de maintenir votre couverture sans interruption, "
                + "nous vous invitons a renouveler votre police avant la date d'echeance.\n\n"
                + "Pour proceder au renouvellement :\n"
                + "  - Connectez-vous a votre espace client\n"
                + "  - Contactez notre service client au 555-1234\n"
                + "  - Visitez l'une de nos agences\n\n"
                + "Cordialement,\nL'equipe AssureFlow\nwww.assureflow.com | contact@assureflow.com";
    }

    private String buildExpiringSmsContent(String policyNumber, Object expirationDate, Object daysUntilExpiry) {
        return String.format(
                "AssureFlow: Votre police %s expire le %s (%s jours). Renouvelez maintenant. Contact: 555-1234",
                policyNumber, expirationDate, daysUntilExpiry);
    }
}
