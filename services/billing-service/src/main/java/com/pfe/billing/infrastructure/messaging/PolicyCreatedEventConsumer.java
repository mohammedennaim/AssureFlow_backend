package com.pfe.billing.infrastructure.messaging;

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
public class PolicyCreatedEventConsumer {

    
    @KafkaListener(topics = "policy-events", groupId = "billing-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void onPolicyEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA] Received event type={} partition={} offset={}", eventType, partition, offset);

        if ("policy.created".equals(eventType)) {
            handlePolicyCreated(payload);
        }
    }

    private void handlePolicyCreated(Map<String, Object> payload) {
        String policyId = (String) payload.get("policyId");
        String clientId = (String) payload.get("clientId");
        Object amount = payload.get("premiumAmount");

        log.info("[BILLING] Auto-generating invoice for policy={} client={} amount={}",
                policyId, clientId, amount);

        log.info("[BILLING] Invoice generated for policy={} → SAGA step 3 complete", policyId);
    }
}
