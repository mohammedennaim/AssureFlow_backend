package com.pfe.billing.infrastructure.messaging;

import com.pfe.billing.domain.event.InvoiceGeneratedEvent;
import com.pfe.billing.domain.event.PaymentReceivedEvent;
import com.pfe.billing.domain.model.Invoice;
import com.pfe.billing.domain.model.Payment;
import com.pfe.billing.domain.repository.InvoiceRepository;
import com.pfe.billing.domain.repository.PaymentRepository;
import com.pfe.billing.infrastructure.client.ClientDto;
import com.pfe.billing.infrastructure.client.ClientServiceClient;
import com.pfe.billing.infrastructure.client.PolicyDto;
import com.pfe.billing.infrastructure.client.PolicyServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Publisher for billing-related Kafka events.
 * Publishes billing events to the "billing-events" topic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BillingEventPublisher {

    private static final String BILLING_TOPIC = "billing-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PolicyServiceClient policyServiceClient;
    private final ClientServiceClient clientServiceClient;

    /**
     * Publishes an invoice generated event.
     * Enriches the event with client data for notifications.
     */
    public void publishInvoiceGenerated(InvoiceGeneratedEvent event) {
        try {
            // Fetch invoice data
            Invoice invoice = invoiceRepository.findById(event.getInvoiceId())
                    .orElse(null);
            
            if (invoice == null) {
                log.warn("[KAFKA] Invoice not found for event: {}", event.getInvoiceId());
                return;
            }

            // Fetch policy and client data
            PolicyDto policy = null;
            ClientDto client = null;
            
            try {
                if (invoice.getPolicyId() != null) {
                    policy = policyServiceClient.getPolicyById(invoice.getPolicyId().toString());
                    if (policy != null && policy.getClientId() != null) {
                        client = clientServiceClient.getClientById(policy.getClientId());
                    }
                }
            } catch (Exception e) {
                log.warn("[KAFKA] Could not fetch policy/client data: {}", e.getMessage());
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("invoiceId", event.getInvoiceId().toString());
            payload.put("policyId", invoice.getPolicyId() != null ? invoice.getPolicyId().toString() : null);
            payload.put("clientId", invoice.getClientId() != null ? invoice.getClientId().toString() : 
                (client != null ? client.getId() : null));
            payload.put("clientEmail", client != null ? client.getEmail() : null);
            payload.put("clientPhone", client != null ? client.getPhone() : null);
            payload.put("amount", invoice.getTotalAmount());
            payload.put("dueDate", invoice.getDueDate() != null ? invoice.getDueDate().toString() : null);
            payload.put("invoiceNumber", invoice.getInvoiceNumber());
            payload.put("timestamp", event.getEventTimestamp() != null ? event.getEventTimestamp().toString() : 
                java.time.LocalDateTime.now().toString());

            String eventType = "invoice.generated";
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(BILLING_TOPIC, eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish InvoiceGeneratedEvent for invoice {}: {}",
                            event.getInvoiceId(), ex.getMessage());
                } else {
                    log.info("[KAFKA] InvoiceGeneratedEvent published → invoice={} topic={} partition={} offset={}",
                            event.getInvoiceId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing InvoiceGeneratedEvent", e);
        }
    }

    /**
     * Publishes a payment received event.
     * Enriches the event with client data for notifications.
     */
    public void publishPaymentReceived(PaymentReceivedEvent event) {
        try {
            // Fetch payment data
            Payment payment = paymentRepository.findById(event.getPaymentId())
                    .orElse(null);
            
            if (payment == null) {
                log.warn("[KAFKA] Payment not found for event: {}", event.getPaymentId());
                return;
            }

            // Fetch invoice and client data
            Invoice invoice = null;
            PolicyDto policy = null;
            ClientDto client = null;
            
            try {
                if (payment.getInvoiceId() != null) {
                    invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);
                    if (invoice != null && invoice.getPolicyId() != null) {
                        policy = policyServiceClient.getPolicyById(invoice.getPolicyId().toString());
                        if (policy != null && policy.getClientId() != null) {
                            client = clientServiceClient.getClientById(policy.getClientId());
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("[KAFKA] Could not fetch invoice/policy/client data: {}", e.getMessage());
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("paymentId", event.getPaymentId().toString());
            payload.put("invoiceId", event.getInvoiceId().toString());
            payload.put("clientId", invoice != null && invoice.getClientId() != null ? 
                invoice.getClientId().toString() : (client != null ? client.getId() : null));
            payload.put("clientEmail", client != null ? client.getEmail() : null);
            payload.put("clientPhone", client != null ? client.getPhone() : null);
            payload.put("amount", payment.getAmount());
            payload.put("paymentMethod", payment.getMethod() != null ? payment.getMethod().name() : null);
            payload.put("timestamp", event.getEventTimestamp() != null ? event.getEventTimestamp().toString() : 
                java.time.LocalDateTime.now().toString());

            String eventType = "payment.received";
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(BILLING_TOPIC, eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish PaymentReceivedEvent for payment {}: {}",
                            event.getPaymentId(), ex.getMessage());
                } else {
                    log.info("[KAFKA] PaymentReceivedEvent published → payment={} topic={} partition={} offset={}",
                            event.getPaymentId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing PaymentReceivedEvent", e);
        }
    }
}
