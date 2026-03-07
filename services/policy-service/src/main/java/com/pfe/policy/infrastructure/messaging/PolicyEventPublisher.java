package com.pfe.policy.infrastructure.messaging;

import com.pfe.policy.domain.event.PolicyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyEventPublisher {

    private static final String POLICY_TOPIC = "policy-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void publishPolicyCreated(PolicyCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(POLICY_TOPIC, "policy.created",
                event);

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
