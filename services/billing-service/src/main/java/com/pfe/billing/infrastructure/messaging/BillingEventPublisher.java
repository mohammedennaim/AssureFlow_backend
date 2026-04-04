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
import com.pfe.commons.dto.BaseResponse;
import com.pfe.commons.messaging.AbstractEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Publisher for billing-related Kafka events.
 * Publishes billing events to the "billing-events" topic.
 */
@Slf4j
@Component
public class BillingEventPublisher extends AbstractEventPublisher {

    private static final String BILLING_TOPIC = "billing-events";
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PolicyServiceClient policyServiceClient;
    private final ClientServiceClient clientServiceClient;

    public BillingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                 InvoiceRepository invoiceRepository,
                                 PaymentRepository paymentRepository,
                                 PolicyServiceClient policyServiceClient,
                                 ClientServiceClient clientServiceClient) {
        super(kafkaTemplate, "billing-service");
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.policyServiceClient = policyServiceClient;
        this.clientServiceClient = clientServiceClient;
    }

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

            // Fetch policy and client data with fallback strategy
            PolicyDto policy = null;
            ClientDto client = null;
            UUID clientIdToUse = null;

            // Priority 1: Use clientId from invoice (UUID)
            if (invoice.getClientId() != null) {
                clientIdToUse = invoice.getClientId();
            }
            // Priority 2: Fallback to policy's clientId (String -> UUID)
            else if (invoice.getPolicyId() != null) {
                try {
                    policy = policyServiceClient.getPolicyById(invoice.getPolicyId().toString());
                    if (policy != null && policy.getClientId() != null) {
                        clientIdToUse = UUID.fromString(policy.getClientId());
                    }
                } catch (Exception e) {
                    log.warn("[KAFKA] Could not fetch policy: {}", e.getMessage());
                }
            }

            // Fetch client data if we have a clientId
            if (clientIdToUse != null) {
                try {
                    client = extractClient(clientServiceClient.getClientById(clientIdToUse.toString()));
                } catch (Exception e) {
                    log.warn("[KAFKA] Could not fetch client {}: {}", clientIdToUse, e.getMessage());
                }
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("invoiceId", event.getInvoiceId().toString());
            payload.put("policyId", invoice.getPolicyId() != null ? invoice.getPolicyId().toString() : null);
            payload.put("clientId", clientIdToUse != null ? clientIdToUse.toString() : null);
            payload.put("clientEmail", client != null ? client.getEmail() : null);
            payload.put("clientPhone", client != null ? client.getPhone() : null);
            payload.put("amount", invoice.getTotalAmount());
            payload.put("dueDate", invoice.getDueDate() != null ? invoice.getDueDate().toString() : null);
            payload.put("invoiceNumber", invoice.getInvoiceNumber());
            payload.put("timestamp", event.getEventTimestamp() != null ? event.getEventTimestamp().toString() :
                java.time.LocalDateTime.now().toString());

            publish(BILLING_TOPIC, "invoice.generated", payload);
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

            // Fetch invoice and client data with fallback strategy
            Invoice invoice = null;
            PolicyDto policy = null;
            ClientDto client = null;
            UUID clientIdToUse = null;

            try {
                if (payment.getInvoiceId() != null) {
                    invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);
                    if (invoice != null) {
                        // Priority 1: Use clientId from invoice (UUID)
                        if (invoice.getClientId() != null) {
                            clientIdToUse = invoice.getClientId();
                        }
                        // Priority 2: Fallback to policy's clientId (String -> UUID)
                        else if (invoice.getPolicyId() != null) {
                            policy = policyServiceClient.getPolicyById(invoice.getPolicyId().toString());
                            if (policy != null && policy.getClientId() != null) {
                                clientIdToUse = UUID.fromString(policy.getClientId());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("[KAFKA] Could not fetch invoice/policy data: {}", e.getMessage());
            }

            // Fetch client data if we have a clientId
            if (clientIdToUse != null) {
                try {
                    client = extractClient(clientServiceClient.getClientById(clientIdToUse.toString()));
                } catch (Exception e) {
                    log.warn("[KAFKA] Could not fetch client {}: {}", clientIdToUse, e.getMessage());
                }
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("paymentId", event.getPaymentId().toString());
            payload.put("invoiceId", event.getInvoiceId().toString());
            payload.put("clientId", clientIdToUse != null ? clientIdToUse.toString() : null);
            payload.put("clientEmail", client != null ? client.getEmail() : null);
            payload.put("clientPhone", client != null ? client.getPhone() : null);
            payload.put("amount", payment.getAmount());
            payload.put("paymentMethod", payment.getMethod() != null ? payment.getMethod().name() : null);
            payload.put("timestamp", event.getEventTimestamp() != null ? event.getEventTimestamp().toString() :
                java.time.LocalDateTime.now().toString());

            publish(BILLING_TOPIC, "payment.received", payload);
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing PaymentReceivedEvent", e);
        }
    }

    private ClientDto extractClient(BaseResponse<ClientDto> response) {
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return response.getData();
    }
}
