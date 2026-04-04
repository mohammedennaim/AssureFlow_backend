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

    /**
     * Listens to saga-events topic — receives step.success / step.failed /
     * compensation.success responses published by policy, claims and billing services.
     */
    @KafkaListener(topics = "saga-events", groupId = "workflow-service-saga-group", containerFactory = "kafkaListenerContainerFactory")
    public void onSagaEvent(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String sagaId,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[SAGA] Received saga event sagaId={} partition={} offset={}", sagaId, partition, offset);

        if (payload == null || payload.isEmpty()) {
            log.warn("[SAGA] Empty saga-events payload, skipping");
            return;
        }

        String eventType = (String) payload.get("eventType");
        String stepIdStr = (String) payload.get("stepId");

        if (eventType == null || stepIdStr == null || sagaId == null) {
            log.warn("[SAGA] Missing required fields in saga-events payload: {}", payload);
            return;
        }

        try {
            UUID sagaUUID = UUID.fromString(sagaId);
            UUID stepUUID = UUID.fromString(stepIdStr);

            switch (eventType) {
                case "step.success" -> {
                    log.info("[SAGA] Step success → sagaId={} stepId={}", sagaId, stepIdStr);
                    sagaOrchestratorService.reportStepSuccess(sagaUUID, stepUUID);
                }
                case "step.failed" -> {
                    String error = (String) payload.getOrDefault("error", "Unknown error");
                    log.warn("[SAGA] Step failed → sagaId={} stepId={} error={}", sagaId, stepIdStr, error);
                    sagaOrchestratorService.reportStepFailure(sagaUUID, stepUUID, error);
                }
                case "compensation.success" -> {
                    log.info("[SAGA] Compensation success → sagaId={} stepId={}", sagaId, stepIdStr);
                    sagaOrchestratorService.reportCompensationSuccess(sagaUUID, stepUUID);
                }
                case "compensation.failed" -> {
                    String error = (String) payload.getOrDefault("error", "Compensation failed");
                    log.error("[SAGA] Compensation failed → sagaId={} stepId={} error={}", sagaId, stepIdStr, error);
                    // Compensation failure is logged but not re-thrown — SAGA is already in failed state
                }
                default -> log.debug("[SAGA] Ignoring unhandled saga event type: {}", eventType);
            }
        } catch (IllegalArgumentException e) {
            log.error("[SAGA] Invalid UUID in saga-events payload sagaId={} stepId={}", sagaId, stepIdStr, e);
        }
    }

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
