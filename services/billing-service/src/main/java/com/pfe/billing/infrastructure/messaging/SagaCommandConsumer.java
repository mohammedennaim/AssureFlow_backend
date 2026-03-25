package com.pfe.billing.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.billing.application.dto.CreateInvoiceRequest;
import com.pfe.billing.application.dto.CreatePaymentRequest;
import com.pfe.billing.application.service.InvoiceService;
import com.pfe.billing.application.service.PaymentService;
import com.pfe.billing.domain.model.PaymentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka consumer for SAGA commands in billing-service.
 * Listens to saga-commands topic and executes billing-related actions.
 * Publishes success/failure events back to Kafka for SAGA orchestration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaCommandConsumer {

    private static final String SAGA_EVENTS_TOPIC = "saga-events";
    
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "saga-commands", groupId = "billing-service-saga-group", containerFactory = "kafkaListenerContainerFactory")
    public void onSagaCommand(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType) {

        log.info("[SAGA] billing-service received command type={}", eventType);

        if (payload == null || payload.isEmpty()) {
            log.warn("[SAGA] Received null or empty payload, skipping");
            return;
        }

        try {
            switch (eventType) {
                case "billing.generateInvoice" -> handleGenerateInvoice(payload);
                case "billing.processPayment" -> handleProcessPayment(payload);
                case "billing.compensate.generateInvoice" -> handleCompensateGenerateInvoice(payload);
                default -> log.debug("[SAGA] Ignoring unhandled command: {}", eventType);
            }
        } catch (Exception e) {
            log.error("[SAGA] Error processing command {}", eventType, e);
            publishStepFailed(
                extractString(payload, "sagaId"),
                extractString(payload, "stepId"),
                eventType,
                e.getMessage()
            );
        }
    }

    private void handleGenerateInvoice(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String policyIdStr = extractString(payload, "policyId");
        String clientIdStr = extractString(payload, "clientId");
        String amountStr = extractString(payload, "amount");
        String dueDateStr = extractString(payload, "dueDate");

        log.info("[SAGA] Executing generateInvoice → sagaId={} stepId={} policyId={}",
                sagaId, stepId, policyIdStr);

        try {
            // Parse UUIDs
            UUID policyId = policyIdStr != null ? UUID.fromString(policyIdStr) : null;
            UUID clientId = clientIdStr != null ? UUID.fromString(clientIdStr) : null;

            // Parse amount
            BigDecimal amount = null;
            if (amountStr != null) {
                try {
                    amount = new BigDecimal(amountStr);
                } catch (NumberFormatException e) {
                    log.warn("[SAGA] Could not parse amount: {}", amountStr);
                }
            }

            // Parse due date
            LocalDate dueDate = null;
            if (dueDateStr != null) {
                try {
                    dueDate = LocalDate.parse(dueDateStr);
                } catch (DateTimeParseException e) {
                    log.warn("[SAGA] Could not parse dueDate: {}", dueDateStr);
                }
            }

            // Create invoice request
            CreateInvoiceRequest request = CreateInvoiceRequest.builder()
                    .policyId(policyId)
                    .clientId(clientId)
                    .amount(amount != null ? amount : BigDecimal.ZERO)
                    .dueDate(dueDate)
                    .taxAmount(BigDecimal.ZERO)
                    .build();

            // Generate invoice via service
            var invoiceDto = invoiceService.createInvoice(request);

            log.info("[SAGA] Invoice generated successfully → invoiceId={} invoiceNumber={} sagaId={} stepId={}",
                    invoiceDto.getId(), invoiceDto.getInvoiceNumber(), sagaId, stepId);

            // Publish success event
            publishStepSuccess(sagaId, stepId, "billing.generateInvoice", Map.of(
                "invoiceId", invoiceDto.getId(),
                "invoiceNumber", invoiceDto.getInvoiceNumber(),
                "policyId", policyIdStr,
                "clientId", clientIdStr,
                "amount", invoiceDto.getTotalAmount(),
                "status", invoiceDto.getStatus()
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to generate invoice → sagaId={} stepId={}", sagaId, stepId, e);
            publishStepFailed(sagaId, stepId, "billing.generateInvoice", e.getMessage());
            throw e;
        }
    }

    private void handleProcessPayment(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String invoiceIdStr = extractString(payload, "invoiceId");
        String amountStr = extractString(payload, "amount");
        String paymentMethod = extractString(payload, "paymentMethod");

        log.info("[SAGA] Executing processPayment → sagaId={} stepId={} invoiceId={}",
                sagaId, stepId, invoiceIdStr);

        try {
            // Parse UUIDs
            UUID invoiceId = invoiceIdStr != null ? UUID.fromString(invoiceIdStr) : null;

            // Parse amount
            BigDecimal amount = null;
            if (amountStr != null) {
                try {
                    amount = new BigDecimal(amountStr);
                } catch (NumberFormatException e) {
                    log.warn("[SAGA] Could not parse amount: {}", amountStr);
                }
            }

            // Create payment request
            CreatePaymentRequest request = CreatePaymentRequest.builder()
                    .invoiceId(invoiceId)
                    .clientId(null)  // Will be fetched from invoice
                    .amount(amount != null ? amount : BigDecimal.ZERO)
                    .method(PaymentMethod.BANK_TRANSFER)
                    .transactionId(null)
                    .build();

            // Process payment via service
            var paymentDto = paymentService.createPayment(request);

            log.info("[SAGA] Payment processed successfully → paymentId={} invoiceId={} sagaId={} stepId={}",
                    paymentDto.getId(), invoiceIdStr, sagaId, stepId);

            // Publish success event
            publishStepSuccess(sagaId, stepId, "billing.processPayment", Map.of(
                "paymentId", paymentDto.getId(),
                "invoiceId", invoiceIdStr,
                "amount", paymentDto.getAmount(),
                "paymentMethod", paymentDto.getMethod() != null ? paymentDto.getMethod().name() : null,
                "status", paymentDto.getStatus()
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to process payment → sagaId={} stepId={}", sagaId, stepId, e);
            publishStepFailed(sagaId, stepId, "billing.processPayment", e.getMessage());
            throw e;
        }
    }

    private void handleCompensateGenerateInvoice(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String invoiceIdStr = extractString(payload, "invoiceId");

        log.warn("[SAGA] Executing compensation for generateInvoice → sagaId={} stepId={} invoiceId={}",
                sagaId, stepId, invoiceIdStr);

        try {
            UUID invoiceId = invoiceIdStr != null ? UUID.fromString(invoiceIdStr) : null;

            // Cancel invoice as compensation
            if (invoiceId != null) {
                invoiceService.cancelInvoice(invoiceId);
                log.info("[SAGA] Invoice cancelled for compensation → invoiceId={} sagaId={} stepId={}",
                        invoiceId, sagaId, stepId);
            }

            // Publish compensation success
            publishCompensationSuccess(sagaId, stepId, "billing.generateInvoice", Map.of(
                "invoiceId", invoiceIdStr,
                "compensationAction", "cancelInvoice"
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to compensate generateInvoice → sagaId={} stepId={}", sagaId, stepId, e);
            publishCompensationFailed(sagaId, stepId, "billing.generateInvoice", e.getMessage());
            throw e;
        }
    }

    /**
     * Publishes a step success event to Kafka.
     */
    private void publishStepSuccess(String sagaId, String stepId, String action, Map<String, Object> data) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "step.success");
            event.put("sagaId", sagaId);
            event.put("stepId", stepId);
            event.put("action", action);
            event.put("serviceName", "billing-service");
            event.put("data", data);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(SAGA_EVENTS_TOPIC, sagaId, event);
            log.info("[SAGA] Published step.success → sagaId={} stepId={} action={}", sagaId, stepId, action);
        } catch (Exception e) {
            log.error("[SAGA] Failed to publish step.success", e);
        }
    }

    /**
     * Publishes a step failed event to Kafka.
     */
    private void publishStepFailed(String sagaId, String stepId, String action, String error) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "step.failed");
            event.put("sagaId", sagaId);
            event.put("stepId", stepId);
            event.put("action", action);
            event.put("serviceName", "billing-service");
            event.put("error", error);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(SAGA_EVENTS_TOPIC, sagaId, event);
            log.warn("[SAGA] Published step.failed → sagaId={} stepId={} action={} error={}",
                    sagaId, stepId, action, error);
        } catch (Exception e) {
            log.error("[SAGA] Failed to publish step.failed", e);
        }
    }

    /**
     * Publishes a compensation success event to Kafka.
     */
    private void publishCompensationSuccess(String sagaId, String stepId, String action, Map<String, Object> data) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "compensation.success");
            event.put("sagaId", sagaId);
            event.put("stepId", stepId);
            event.put("action", action);
            event.put("serviceName", "billing-service");
            event.put("data", data);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(SAGA_EVENTS_TOPIC, sagaId, event);
            log.info("[SAGA] Published compensation.success → sagaId={} stepId={} action={}", sagaId, stepId, action);
        } catch (Exception e) {
            log.error("[SAGA] Failed to publish compensation.success", e);
        }
    }

    /**
     * Publishes a compensation failed event to Kafka.
     */
    private void publishCompensationFailed(String sagaId, String stepId, String action, String error) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "compensation.failed");
            event.put("sagaId", sagaId);
            event.put("stepId", stepId);
            event.put("action", action);
            event.put("serviceName", "billing-service");
            event.put("error", error);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(SAGA_EVENTS_TOPIC, sagaId, event);
            log.warn("[SAGA] Published compensation.failed → sagaId={} stepId={} action={}",
                    sagaId, stepId, action);
        } catch (Exception e) {
            log.error("[SAGA] Failed to publish compensation.failed", e);
        }
    }

    /**
     * Helper method to safely extract a string from payload.
     */
    private String extractString(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value != null ? value.toString() : null;
    }
}
