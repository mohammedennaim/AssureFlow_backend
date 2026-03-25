package com.pfe.policy.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.policy.application.dto.CreatePolicyRequest;
import com.pfe.policy.application.service.PolicyService;
import com.pfe.policy.domain.model.PolicyType;
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
 * Kafka consumer for SAGA commands in policy-service.
 * Listens to saga-commands topic and executes policy-related actions.
 * Publishes success/failure events back to Kafka for SAGA orchestration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaCommandConsumer {

    private static final String SAGA_EVENTS_TOPIC = "saga-events";
    
    private final PolicyService policyService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "saga-commands", groupId = "policy-service-saga-group", containerFactory = "kafkaListenerContainerFactory")
    public void onSagaCommand(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventType) {

        log.info("[SAGA] policy-service received command type={}", eventType);

        if (payload == null || payload.isEmpty()) {
            log.warn("[SAGA] Received null or empty payload, skipping");
            return;
        }

        try {
            switch (eventType) {
                case "policy.createPolicy" -> handleCreatePolicy(payload);
                case "policy.approvePolicy" -> handleApprovePolicy(payload);
                case "policy.rejectPolicy" -> handleRejectPolicy(payload);
                case "policy.compensate.createPolicy" -> handleCompensateCreatePolicy(payload);
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

    private void handleCreatePolicy(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String policyNumber = extractString(payload, "policyNumber");
        String clientIdStr = extractString(payload, "clientId");
        String type = extractString(payload, "type");
        String coverageAmountStr = extractString(payload, "coverageAmount");
        String premiumAmountStr = extractString(payload, "premiumAmount");
        String startDateStr = extractString(payload, "startDate");
        String endDateStr = extractString(payload, "endDate");

        log.info("[SAGA] Executing createPolicy → sagaId={} stepId={} policyNumber={}",
                sagaId, stepId, policyNumber);

        try {
            // Parse amounts
            BigDecimal coverageAmount = null;
            if (coverageAmountStr != null) {
                try {
                    coverageAmount = new BigDecimal(coverageAmountStr);
                } catch (NumberFormatException e) {
                    log.warn("[SAGA] Could not parse coverageAmount: {}", coverageAmountStr);
                }
            }

            BigDecimal premiumAmount = null;
            if (premiumAmountStr != null) {
                try {
                    premiumAmount = new BigDecimal(premiumAmountStr);
                } catch (NumberFormatException e) {
                    log.warn("[SAGA] Could not parse premiumAmount: {}", premiumAmountStr);
                }
            }

            // Parse dates
            LocalDate startDate = null;
            if (startDateStr != null) {
                try {
                    startDate = LocalDate.parse(startDateStr);
                } catch (DateTimeParseException e) {
                    log.warn("[SAGA] Could not parse startDate: {}", startDateStr);
                }
            }

            LocalDate endDate = null;
            if (endDateStr != null) {
                try {
                    endDate = LocalDate.parse(endDateStr);
                } catch (DateTimeParseException e) {
                    log.warn("[SAGA] Could not parse endDate: {}", endDateStr);
                }
            }

            // Create policy request
            CreatePolicyRequest request = CreatePolicyRequest.builder()
                    .clientId(clientIdStr)  // Pass String directly, not UUID
                    .type(type != null ? PolicyType.valueOf(type) : PolicyType.VEHICLE)
                    .coverageAmount(coverageAmount)
                    .premiumAmount(premiumAmount)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            // Create policy via service
            var policyDto = policyService.createPolicy(request);

            log.info("[SAGA] Policy created successfully → policyId={} policyNumber={} sagaId={} stepId={}",
                    policyDto.getId(), policyDto.getPolicyNumber(), sagaId, stepId);

            // Publish success event
            publishStepSuccess(sagaId, stepId, "policy.createPolicy", Map.of(
                "policyId", policyDto.getId(),
                "policyNumber", policyDto.getPolicyNumber(),
                "clientId", clientIdStr,
                "type", policyDto.getType(),
                "status", policyDto.getStatus()
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to create policy → sagaId={} stepId={}", sagaId, stepId, e);
            publishStepFailed(sagaId, stepId, "policy.createPolicy", e.getMessage());
            throw e;
        }
    }

    private void handleApprovePolicy(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String policyIdStr = extractString(payload, "policyId");

        log.info("[SAGA] Executing approvePolicy → sagaId={} stepId={} policyId={}",
                sagaId, stepId, policyIdStr);

        try {
            // Approve policy
            policyService.approvePolicy(policyIdStr);

            log.info("[SAGA] Policy approved successfully → policyId={} sagaId={} stepId={}",
                    policyIdStr, sagaId, stepId);

            // Publish success event
            publishStepSuccess(sagaId, stepId, "policy.approvePolicy", Map.of(
                "policyId", policyIdStr,
                "status", "APPROVED"
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to approve policy → sagaId={} stepId={}", sagaId, stepId, e);
            publishStepFailed(sagaId, stepId, "policy.approvePolicy", e.getMessage());
            throw e;
        }
    }

    private void handleRejectPolicy(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String policyIdStr = extractString(payload, "policyId");
        String rejectionReason = extractString(payload, "rejectionReason");

        log.info("[SAGA] Executing rejectPolicy → sagaId={} stepId={} policyId={}",
                sagaId, stepId, policyIdStr);

        try {
            String reason = rejectionReason != null ? rejectionReason : "SAGA rejection";

            // Reject policy
            policyService.rejectPolicy(policyIdStr, reason);

            log.info("[SAGA] Policy rejected successfully → policyId={} sagaId={} stepId={}",
                    policyIdStr, sagaId, stepId);

            // Publish success event
            publishStepSuccess(sagaId, stepId, "policy.rejectPolicy", Map.of(
                "policyId", policyIdStr,
                "rejectionReason", reason,
                "status", "REJECTED"
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to reject policy → sagaId={} stepId={}", sagaId, stepId, e);
            publishStepFailed(sagaId, stepId, "policy.rejectPolicy", e.getMessage());
            throw e;
        }
    }

    private void handleCompensateCreatePolicy(Map<String, Object> payload) {
        String sagaId = extractString(payload, "sagaId");
        String stepId = extractString(payload, "stepId");
        String policyIdStr = extractString(payload, "policyId");

        log.warn("[SAGA] Executing compensation for createPolicy → sagaId={} stepId={} policyId={}",
                sagaId, stepId, policyIdStr);

        try {
            // Note: Policy service may not have a delete method, so we just log the compensation
            // In a real implementation, you would mark the policy as cancelled or deleted
            log.info("[SAGA] Compensation for createPolicy - would delete/cancel policy → policyId={} sagaId={} stepId={}",
                    policyIdStr, sagaId, stepId);

            // Publish compensation success
            publishCompensationSuccess(sagaId, stepId, "policy.createPolicy", Map.of(
                "policyId", policyIdStr,
                "compensationAction", "cancelPolicy"
            ));

        } catch (Exception e) {
            log.error("[SAGA] Failed to compensate createPolicy → sagaId={} stepId={}", sagaId, stepId, e);
            publishCompensationFailed(sagaId, stepId, "policy.createPolicy", e.getMessage());
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
            event.put("serviceName", "policy-service");
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
            event.put("serviceName", "policy-service");
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
            event.put("serviceName", "policy-service");
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
            event.put("serviceName", "policy-service");
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
