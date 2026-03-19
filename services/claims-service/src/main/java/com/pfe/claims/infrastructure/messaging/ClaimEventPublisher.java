package com.pfe.claims.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishClaimCreated(UUID claimId, UUID policyId, UUID clientId, LocalDateTime createdAt, LocalDateTime slaDeadline) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "CLAIM_CREATED");
            event.put("claimId", claimId.toString());
            event.put("policyId", policyId.toString());
            event.put("clientId", clientId.toString());
            event.put("createdAt", createdAt.toString());
            event.put("slaDeadline", slaDeadline.toString());
            event.put("timestamp", LocalDateTime.now().toString());

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("claim-events", claimId.toString(), message);
            
            log.info("Published CLAIM_CREATED event for claim: {}", claimId);
        } catch (Exception e) {
            log.error("Failed to publish CLAIM_CREATED event", e);
        }
    }

    public void publishClaimStatusChanged(UUID claimId, String oldStatus, String newStatus, UUID userId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "CLAIM_STATUS_CHANGED");
            event.put("claimId", claimId.toString());
            event.put("oldStatus", oldStatus);
            event.put("newStatus", newStatus);
            event.put("userId", userId != null ? userId.toString() : null);
            event.put("timestamp", LocalDateTime.now().toString());

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("claim-events", claimId.toString(), message);
            
            log.info("Published CLAIM_STATUS_CHANGED event for claim: {} ({} -> {})", claimId, oldStatus, newStatus);
        } catch (Exception e) {
            log.error("Failed to publish CLAIM_STATUS_CHANGED event", e);
        }
    }
}
