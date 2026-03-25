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

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final NotificationServiceImpl notificationService;
    private final TwilioSmsService twilioSmsService;

    @KafkaListener(topics = "billing-events", groupId = "notification-service-billing-group-v2", containerFactory = "kafkaListenerContainerFactory")
    public void onBillingEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA] notification-service received billing event type={} partition={} offset={}",
                eventType, partition, offset);

        switch (eventType) {
            case "payment.received" -> handlePaymentReceived(payload);
            case "invoice.generated" -> handleInvoiceGenerated(payload);
            case "invoice.overdue"   -> handleInvoiceOverdue(payload);
            default -> log.debug("[NOTIFICATION] Ignoring unhandled billing event: {}", eventType);
        }
    }

    private void handlePaymentReceived(Map<String, Object> payload) {
        String clientId  = (String) payload.get("clientId");
        String invoiceId = (String) payload.get("invoiceId");
        Object amount    = payload.get("amount");
        String clientPhone = (String) payload.get("clientPhone");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.PAYMENT_RECEIVED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Paiement reçu")
                .content("Nous avons bien reçu votre paiement de " + amount + " pour la facture " + invoiceId + ".")
                .build();
        var emailDto = notificationService.createNotificationInternal(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.PAYMENT_RECEIVED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Paiement de " + amount + " reçu pour facture " + invoiceId + ". Merci!")
                    .build();
            var smsDto = notificationService.createNotificationInternal(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] PAYMENT_RECEIVED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] PAYMENT_RECEIVED email sent to {}", recipient);
    }

    private void handleInvoiceGenerated(Map<String, Object> payload) {
        String clientId  = (String) payload.get("clientId");
        String invoiceId = (String) payload.get("invoiceId");
        Object dueDate   = payload.get("dueDate");
        Object amount    = payload.get("amount");
        String clientPhone = (String) payload.get("clientPhone");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.INVOICE_GENERATED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Nouvelle facture générée")
                .content("Une facture " + invoiceId + " a été générée. Échéance : " + dueDate + ".")
                .build();
        var emailDto = notificationService.createNotificationInternal(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.INVOICE_GENERATED)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("Facture " + invoiceId + " générée. Montant: " + amount + ". Échéance: " + dueDate + ".")
                    .build();
            var smsDto = notificationService.createNotificationInternal(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] INVOICE_GENERATED SMS sent to {}", clientPhone);
        }
        
        log.info("[NOTIFICATION] INVOICE_GENERATED email sent to {}", recipient);
    }

    private void handleInvoiceOverdue(Map<String, Object> payload) {
        String clientId  = (String) payload.get("clientId");
        String invoiceId = (String) payload.get("invoiceId");
        Object amount    = payload.get("amount");
        String clientPhone = (String) payload.get("clientPhone");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        
        // Create EMAIL notification
        CreateNotificationRequest emailRequest = CreateNotificationRequest.builder()
                .type(NotificationType.INVOICE_OVERDUE)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Facture en retard de paiement")
                .content("Rappel : votre facture " + invoiceId + " est en retard. Merci de régulariser votre situation.")
                .build();
        var emailDto = notificationService.createNotificationInternal(emailRequest);
        notificationService.sendNotification(emailDto.getId());
        
        // Create SMS notification if phone number is available
        if (clientPhone != null && !clientPhone.isBlank()) {
            CreateNotificationRequest smsRequest = CreateNotificationRequest.builder()
                    .type(NotificationType.INVOICE_OVERDUE)
                    .channel(NotificationChannel.SMS)
                    .recipient(clientPhone)
                    .content("RAPPEL: Facture " + invoiceId + " en retard. Montant: " + amount + ". Merci de régulariser.")
                    .build();
            var smsDto = notificationService.createNotificationInternal(smsRequest);
            notificationService.sendNotification(smsDto.getId());
            log.info("[NOTIFICATION] INVOICE_OVERDUE SMS sent to {}", clientPhone);
        }
        
        log.warn("[NOTIFICATION] INVOICE_OVERDUE email sent to {}", recipient);
    }
}
