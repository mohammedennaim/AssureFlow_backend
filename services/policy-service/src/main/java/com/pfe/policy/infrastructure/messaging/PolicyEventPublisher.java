package com.pfe.policy.infrastructure.messaging;

import com.pfe.policy.domain.event.PolicyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyEventPublisher {

    private static final String POLICY_TOPIC = "policy-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void publishPolicyCreated(PolicyCreatedEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("policyId", event.getPolicyId());
        payload.put("policyNumber", event.getPolicyNumber());
        payload.put("clientId", event.getClientId());
        payload.put("clientEmail", event.getClientEmail());
        payload.put("clientPhone", event.getClientPhone());
        payload.put("type", event.getType());
        payload.put("status", event.getStatus());
        payload.put("premiumAmount", event.getPremiumAmount());
        payload.put("coverageAmount", event.getCoverageAmount());
        payload.put("startDate", event.getStartDate());
        payload.put("endDate", event.getEndDate());
        payload.put("timestamp", java.time.LocalDateTime.now().toString());

        // Send as Map<String, Object> to ensure proper deserialization in consumer
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(POLICY_TOPIC, "policy.created", payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[KAFKA] Failed to publish PolicyCreatedEvent for policy {}: {}",
                        event.getPolicyId(), ex.getMessage());
            } else {
                log.info("[KAFKA] PolicyCreatedEvent published for policy {} → topic={}, partition={}, offset={}",
                        event.getPolicyId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    public void publishPolicyEvent(String eventType, Object event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(POLICY_TOPIC, eventType, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[KAFKA] Failed to publish {} event: {}", eventType, ex.getMessage());
            } else {
                log.info("[KAFKA] {} event published → topic={}", eventType, POLICY_TOPIC);
            }
        });
    }
}
