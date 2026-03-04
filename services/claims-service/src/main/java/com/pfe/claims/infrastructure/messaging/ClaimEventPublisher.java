package com.pfe.claims.infrastructure.messaging;

import com.pfe.claims.domain.event.ClaimSubmittedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClaimEventPublisher {
    private static final String CLAIM_TOPIC = "claim-events";

    public void publishClaimSubmitted(ClaimSubmittedEvent event) {
        log.info("[EVENT] ClaimSubmittedEvent for claim: {} (Kafka integration pending)", event.getClaimId());
    }

    public void publishClaimEvent(String eventType, Object event) {
        log.info("[EVENT] {} event published (Kafka integration pending)", eventType);
    }
}
