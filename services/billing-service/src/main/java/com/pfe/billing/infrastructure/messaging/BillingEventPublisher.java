package com.pfe.billing.infrastructure.messaging;

import com.pfe.billing.domain.event.InvoiceGeneratedEvent;
import com.pfe.billing.domain.event.PaymentReceivedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingEventPublisher {

    private static final String BILLING_TOPIC = "billing-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInvoiceGenerated(InvoiceGeneratedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(BILLING_TOPIC, "invoice.generated", event);
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
    }

    public void publishPaymentReceived(PaymentReceivedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(BILLING_TOPIC, "payment.received", event);
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
    }
}
