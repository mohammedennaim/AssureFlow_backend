package com.pfe.workflow.infrastructure.messaging;

import com.pfe.workflow.application.service.SAGAOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Kafka consumer for the workflow-service.
 * Listens to domain events from claims, policy, and billing services
 * to advance the SAGA state machine steps.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SAGAEventConsumer {

    private final SAGAOrchestratorService sagaOrchestratorService;

    @KafkaListener(topics = "claim-events", groupId = "workflow-service-claim-group", containerFactory = "kafkaListenerContainerFactory")
    public void onClaimEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[SAGA] Received claim event type={} partition={} offset={}", eventType, partition, offset);

        switch (eventType) {
            case "claim.submitted" -> {
                String sagaId = (String) payload.get("sagaId");
                String stepId = (String) payload.get("stepId");
                if (sagaId != null && stepId != null) {
                    log.info("[SAGA] Advancing step on claim.submitted → sagaId={} stepId={}", sagaId, stepId);
                    sagaOrchestratorService.reportStepSuccess(UUID.fromString(sagaId), UUID.fromString(stepId));
                }
            }
            case "claim.rejected" -> {
                String sagaId = (String) payload.get("sagaId");
                String stepId = (String) payload.get("stepId");
                String errorMsg = (String) payload.getOrDefault("reason", "Claim rejected");
                if (sagaId != null && stepId != null) {
                    log.warn("[SAGA] Step failed on claim.rejected → sagaId={} stepId={}", sagaId, stepId);
                    sagaOrchestratorService.reportStepFailure(UUID.fromString(sagaId), UUID.fromString(stepId),
                            errorMsg);
                }
            }
            default -> log.debug("[SAGA] Ignoring unhandled claim event: {}", eventType);
        }
    }

    @KafkaListener(topics = "policy-events", groupId = "workflow-service-policy-group", containerFactory = "kafkaListenerContainerFactory")
    public void onPolicyEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[SAGA] Received policy event type={} partition={} offset={}", eventType, partition, offset);

        if ("policy.created".equals(eventType)) {
            String sagaId = (String) payload.get("sagaId");
            String stepId = (String) payload.get("stepId");
            if (sagaId != null && stepId != null) {
                log.info("[SAGA] Advancing step on policy.created → sagaId={}", sagaId);
                sagaOrchestratorService.reportStepSuccess(UUID.fromString(sagaId), UUID.fromString(stepId));
            }
        }
    }
}
