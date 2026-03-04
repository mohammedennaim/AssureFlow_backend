package com.pfe.billing.infrastructure.messaging;

import com.pfe.billing.domain.event.InvoiceGeneratedEvent;
import com.pfe.billing.domain.event.PaymentReceivedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BillingEventPublisher {
    private static final String BILLING_TOPIC = "billing-events";

    public void publishInvoiceGenerated(InvoiceGeneratedEvent event) {
        log.info("[EVENT] InvoiceGeneratedEvent for invoice: {} (Kafka integration pending)", event.getInvoiceId());
    }

    public void publishPaymentReceived(PaymentReceivedEvent event) {
        log.info("[EVENT] PaymentReceivedEvent for payment: {} (Kafka integration pending)", event.getPaymentId());
    }
}
