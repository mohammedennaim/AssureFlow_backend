package com.pfe.claims.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.claims.application.dto.CreateClaimRequest;
import com.pfe.claims.application.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka consumer for SAGA commands in claims-service.
 * Listens to saga-commands topic and executes claim-related actions.
 * Publishes success/failure events back to Kafka for SAGA orchestration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaCommandConsumer {

    private static final String SAGA_EVENTS_TOPIC = "saga-events";
    
    private final ClaimService claimService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "saga-commands", groupId = "claims-service-saga-group", containerFactory = "kafkaListenerContainerFactory")
    public void onSagaCommand(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType) {

        log.info("[SAGA] claims-service received command type={}", eventType);

        if (payload == null || payload.isEmpty()) {
            log.warn("[SAGA] Received null or empty payload, skipping");
            return;
        }

        try {
            switch (eventType) {
                case "claims.createClaim" -> handleCreateClaim(payload);
                case "claims.approveClaim" -> handleApproveClaim(payload);
                case "claims.rejectClaim" -> handleRejectClaim(payload);
                case "claims.compensate.createClaim" -> handleCompensateCreateClaim(payload);
                default -> log.debug("[SAGA] Ignoring unhandled command: {}", eventType);
            }
        } catch (Exception e) {
            log.error("[SAGA] Error processing command {}", eventType, e);
            publishStepFailed(
                extractString(payload, "sagaId"),
                extractString(payload, "stepId"),
                eventType,
                e.getMessage()
            );
        }
    }

    private void handleCreateClaim(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String claimNumber = extractString(payload, "claimNumber");
        String policyIdStr = extractString(payload, "policyId");
        String clientIdStr = extractString(payload, "clientId");
        String description = extractString(payload, "description");
        String incidentDateStr = extractString(payload, "incidentDate");

        log.info("[SAGA] Executing createClaim → sagaId={} stepId={} claimNumber={}",
                sagaId, stepId, claimNumber);

        try {
            // Parse UUIDs
            UUID policyId = policyIdStr != null ? UUID.fromString(policyIdStr) : null;
            UUID clientId = clientIdStr != null ? UUID.fromString(clientIdStr) : null;

            // Parse incident date
            LocalDate incidentDate = null;
            if (incidentDateStr != null) {
                try {
                    incidentDate = LocalDate.parse(incidentDateStr);
                } catch (DateTimeParseException e) {
                    log.warn("[SAGA] Could not parse incidentDate: {}", incidentDateStr);
                }
            }

            // Create claim request
            CreateClaimRequest request = CreateClaimRequest.builder()
                    .policyId(policyId)
                    .description(description != null ? description : "SAGA claim creation")
                    .incidentDate(incidentDate)
                    .estimatedAmount(null)
                    .build();

            // Create claim via service
            var claimDto = claimService.createClaim(request);

            log.info("[SAGA] Claim created successfully → claimId={} claimNumber={} sagaId={} stepId={}",
                    claimDto.getId(), claimDto.getClaimNumber(), sagaId, stepId);

            // Publish success event
            publishStepSuccess(sagaId, stepId, "claims.createClaim", Map.of(
                "claimId", claimDto.getId(),
                "claimNumber", claimDto.getClaimNumber(),
                "policyId", policyIdStr,
                "clientId", clientIdStr,
                "status", claimDto.getStatus()
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to create claim → sagaId={} stepId={}", sagaId, stepId, e);
            publishStepFailed(sagaId, stepId, "claims.createClaim", e.getMessage());
            throw e;
        }
    }

    private void handleApproveClaim(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String claimIdStr = extractString(payload, "claimId");
        String approvedAmountStr = extractString(payload, "approvedAmount");

        log.info("[SAGA] Executing approveClaim → sagaId={} stepId={} claimId={}",
                sagaId, stepId, claimIdStr);

        try {
            UUID claimId = claimIdStr != null ? UUID.fromString(claimIdStr) : null;
            BigDecimal approvedAmount = null;
            
            if (approvedAmountStr != null) {
                try {
                    approvedAmount = new BigDecimal(approvedAmountStr);
                } catch (NumberFormatException e) {
                    log.warn("[SAGA] Could not parse approvedAmount: {}", approvedAmountStr);
                }
            }

            // Approve claim (approvedBy is null for SAGA)
            claimService.approveClaim(claimId, approvedAmount, null);

            log.info("[SAGA] Claim approved successfully → claimId={} sagaId={} stepId={}",
                    claimId, sagaId, stepId);

            // Publish success event
            publishStepSuccess(sagaId, stepId, "claims.approveClaim", Map.of(
                "claimId", claimIdStr,
                "approvedAmount", approvedAmount,
                "status", "APPROVED"
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to approve claim → sagaId={} stepId={}", sagaId, stepId, e);
            publishStepFailed(sagaId, stepId, "claims.approveClaim", e.getMessage());
            throw e;
        }
    }

    private void handleRejectClaim(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String claimIdStr = extractString(payload, "claimId");
        String rejectionReason = extractString(payload, "rejectionReason");

        log.info("[SAGA] Executing rejectClaim → sagaId={} stepId={} claimId={}",
                sagaId, stepId, claimIdStr);

        try {
            UUID claimId = claimIdStr != null ? UUID.fromString(claimIdStr) : null;
            String reason = rejectionReason != null ? rejectionReason : "SAGA rejection";

            // Reject claim
            claimService.rejectClaim(claimId, reason);

            log.info("[SAGA] Claim rejected successfully → claimId={} sagaId={} stepId={}",
                    claimId, sagaId, stepId);

            // Publish success event
            publishStepSuccess(sagaId, stepId, "claims.rejectClaim", Map.of(
                "claimId", claimIdStr,
                "rejectionReason", reason,
                "status", "REJECTED"
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to reject claim → sagaId={} stepId={}", sagaId, stepId, e);
            publishStepFailed(sagaId, stepId, "claims.rejectClaim", e.getMessage());
            throw e;
        }
    }

    private void handleCompensateCreateClaim(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String claimIdStr = extractString(payload, "claimId");

        log.warn("[SAGA] Executing compensation for createClaim → sagaId={} stepId={} claimId={}",
                sagaId, stepId, claimIdStr);

        try {
            UUID claimId = claimIdStr != null ? UUID.fromString(claimIdStr) : null;

            // Delete the claim as compensation
            if (claimId != null) {
                claimService.deleteClaim(claimId);
                log.info("[SAGA] Claim deleted for compensation → claimId={} sagaId={} stepId={}",
                        claimId, sagaId, stepId);
            }

            // Publish compensation success
            publishCompensationSuccess(sagaId, stepId, "claims.createClaim", Map.of(
                "claimId", claimIdStr,
                "compensationAction", "deleteClaim"
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to compensate createClaim → sagaId={} stepId={}", sagaId, stepId, e);
            publishCompensationFailed(sagaId, stepId, "claims.createClaim", e.getMessage());
            throw e;
        }
    }

    /**
     * Publishes a step success event to Kafka.
     */
    private void publishStepSuccess(String sagaId, String stepId, String action, Map<String, Object> data) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "step.success");
            event.put("sagaId", sagaId);
            event.put("stepId", stepId);
            event.put("action", action);
            event.put("serviceName", "claims-service");
            event.put("data", data);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(SAGA_EVENTS_TOPIC, sagaId, event);
            log.info("[SAGA] Published step.success → sagaId={} stepId={} action={}", sagaId, stepId, action);
        } catch (Exception e) {
            log.error("[SAGA] Failed to publish step.success", e);
        }
    }

    /**
     * Publishes a step failed event to Kafka.
     */
    private void publishStepFailed(String sagaId, String stepId, String action, String error) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "step.failed");
            event.put("sagaId", sagaId);
            event.put("stepId", stepId);
            event.put("action", action);
            event.put("serviceName", "claims-service");
            event.put("error", error);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(SAGA_EVENTS_TOPIC, sagaId, event);
            log.warn("[SAGA] Published step.failed → sagaId={} stepId={} action={} error={}",
                    sagaId, stepId, action, error);
        } catch (Exception e) {
            log.error("[SAGA] Failed to publish step.failed", e);
        }
    }

    /**
     * Publishes a compensation success event to Kafka.
     */
    private void publishCompensationSuccess(String sagaId, String stepId, String action, Map<String, Object> data) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "compensation.success");
            event.put("sagaId", sagaId);
            event.put("stepId", stepId);
            event.put("action", action);
            event.put("serviceName", "claims-service");
            event.put("data", data);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(SAGA_EVENTS_TOPIC, sagaId, event);
            log.info("[SAGA] Published compensation.success → sagaId={} stepId={} action={}", sagaId, stepId, action);
        } catch (Exception e) {
            log.error("[SAGA] Failed to publish compensation.success", e);
        }
    }

    /**
     * Publishes a compensation failed event to Kafka.
     */
    private void publishCompensationFailed(String sagaId, String stepId, String action, String error) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "compensation.failed");
            event.put("sagaId", sagaId);
            event.put("stepId", stepId);
            event.put("action", action);
            event.put("serviceName", "claims-service");
            event.put("error", error);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(SAGA_EVENTS_TOPIC, sagaId, event);
            log.warn("[SAGA] Published compensation.failed → sagaId={} stepId={} action={}",
                    sagaId, stepId, action);
        } catch (Exception e) {
            log.error("[SAGA] Failed to publish compensation.failed", e);
        }
    }

    /**
     * Helper method to safely extract a string from payload.
     */
    private String extractString(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value != null ? value.toString() : null;
    }
}
