package com.pfe.claims.infrastructure.messaging;

import com.pfe.claims.domain.event.ClaimSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimEventPublisher {

    private static final String CLAIM_TOPIC = "claim-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishClaimSubmitted(ClaimSubmittedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(CLAIM_TOPIC, "claim.submitted",
                event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[KAFKA] Failed to publish ClaimSubmittedEvent for claim {}: {}",
                        event.getClaimId(), ex.getMessage());
            } else {
                log.info("[KAFKA] ClaimSubmittedEvent published for claim {} -> topic={} partition={} offset={}",
                        event.getClaimId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    public void publishClaimEvent(String eventType, Object event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(CLAIM_TOPIC, eventType, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[KAFKA] Failed to publish {} event: {}", eventType, ex.getMessage());
            } else {
                log.info("[KAFKA] {} event published -> topic={}", eventType, CLAIM_TOPIC);
            }
        });
    }
}
