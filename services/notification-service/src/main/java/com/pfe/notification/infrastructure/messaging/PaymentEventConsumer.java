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
public class PaymentEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "billing-events", groupId = "notification-service-billing-group", containerFactory = "kafkaListenerContainerFactory")
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
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .type(NotificationType.PAYMENT_RECEIVED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Paiement reçu")
                .content("Nous avons bien reçu votre paiement de " + amount + " pour la facture " + invoiceId + ".")
                .build();
        var dto = notificationService.createNotification(request);
        notificationService.sendNotification(dto.getId());
        log.info("[NOTIFICATION] PAYMENT_RECEIVED notification sent to {}", recipient);
    }

    private void handleInvoiceGenerated(Map<String, Object> payload) {
        String clientId  = (String) payload.get("clientId");
        String invoiceId = (String) payload.get("invoiceId");
        Object dueDate   = payload.get("dueDate");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .type(NotificationType.INVOICE_GENERATED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Nouvelle facture générée")
                .content("Une facture " + invoiceId + " a été générée. Échéance : " + dueDate + ".")
                .build();
        var dto = notificationService.createNotification(request);
        notificationService.sendNotification(dto.getId());
        log.info("[NOTIFICATION] INVOICE_GENERATED notification sent to {}", recipient);
    }

    private void handleInvoiceOverdue(Map<String, Object> payload) {
        String clientId  = (String) payload.get("clientId");
        String invoiceId = (String) payload.get("invoiceId");
        String recipient = (String) payload.getOrDefault("clientEmail", clientId);
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .type(NotificationType.INVOICE_GENERATED)
                .channel(NotificationChannel.EMAIL)
                .recipient(recipient)
                .subject("Facture en retard de paiement")
                .content("Rappel : votre facture " + invoiceId + " est en retard. Merci de régulariser votre situation.")
                .build();
        var dto = notificationService.createNotification(request);
        notificationService.sendNotification(dto.getId());
        log.warn("[NOTIFICATION] INVOICE_OVERDUE reminder sent to {}", recipient);
    }
}
