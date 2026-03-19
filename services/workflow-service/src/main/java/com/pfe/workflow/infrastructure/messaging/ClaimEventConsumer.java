package com.pfe.workflow.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.workflow.application.dto.CreateAuditRequest;
import com.pfe.workflow.application.service.AuditService;
import com.pfe.workflow.application.service.SLAService;
import com.pfe.workflow.application.service.SLAServiceImpl;
import com.pfe.workflow.domain.model.AuditAction;
import com.pfe.workflow.domain.model.SLADefinition;
import com.pfe.workflow.domain.repository.SLADefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimEventConsumer {

    private final ObjectMapper objectMapper;
    private final AuditService auditService;
    private final SLAService slaService;
    private final SLAServiceImpl slaServiceImpl;
    private final SLADefinitionRepository slaDefinitionRepository;

    @KafkaListener(topics = "claim-events", groupId = "workflow-service")
    public void consumeClaimEvent(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            String eventType = (String) event.get("eventType");

            log.info("Received claim event: {}", eventType);

            switch (eventType) {
                case "CLAIM_CREATED":
                    handleClaimCreated(event);
                    break;
                case "CLAIM_STATUS_CHANGED":
                    handleClaimStatusChanged(event);
                    break;
                default:
                    log.warn("Unknown event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing claim event", e);
        }
    }

    private void handleClaimCreated(Map<String, Object> event) {
        UUID claimId = UUID.fromString((String) event.get("claimId"));
        LocalDateTime createdAt = LocalDateTime.parse((String) event.get("createdAt"));
        LocalDateTime slaDeadline = LocalDateTime.parse((String) event.get("slaDeadline"));

        // Track SLA
        slaService.trackSLA(claimId, "CLAIM", createdAt);

        // Create audit log
        CreateAuditRequest auditRequest = CreateAuditRequest.builder()
                .userId(UUID.randomUUID()) // System user
                .username("SYSTEM")
                .action(AuditAction.CLAIM_CREATED)
                .entityType("CLAIM")
                .entityId(claimId)
                .newValue("Claim created with SLA deadline: " + slaDeadline)
                .build();

        auditService.createAuditLog(auditRequest);

        // Check if SLA is already violated (for backdated claims)
        if (LocalDateTime.now().isAfter(slaDeadline)) {
            Optional<SLADefinition> definition = slaDefinitionRepository.findByEntityTypeAndActive("CLAIM", true);
            definition.ifPresent(def -> 
                slaServiceImpl.createViolation(claimId, "CLAIM", def, createdAt)
            );
        }

        log.info("Tracked SLA for claim: {}, deadline: {}", claimId, slaDeadline);
    }

    private void handleClaimStatusChanged(Map<String, Object> event) {
        UUID claimId = UUID.fromString((String) event.get("claimId"));
        String oldStatus = (String) event.get("oldStatus");
        String newStatus = (String) event.get("newStatus");
        String userIdStr = (String) event.get("userId");
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : UUID.randomUUID();

        // Create audit log
        CreateAuditRequest auditRequest = CreateAuditRequest.builder()
                .userId(userId)
                .username("USER") // Should be fetched from IAM service
                .action(mapStatusToAuditAction(newStatus))
                .entityType("CLAIM")
                .entityId(claimId)
                .oldValue(oldStatus)
                .newValue(newStatus)
                .build();

        auditService.createAuditLog(auditRequest);

        log.info("Audited claim status change: {} ({} -> {})", claimId, oldStatus, newStatus);
    }

    private AuditAction mapStatusToAuditAction(String status) {
        return switch (status) {
            case "SUBMITTED" -> AuditAction.CLAIM_SUBMITTED;
            case "UNDER_REVIEW" -> AuditAction.CLAIM_REVIEWED;
            case "APPROVED" -> AuditAction.CLAIM_APPROVED;
            case "REJECTED" -> AuditAction.CLAIM_REJECTED;
            case "PAYOUT_INITIATED" -> AuditAction.CLAIM_PAYOUT_INITIATED;
            case "PAID" -> AuditAction.CLAIM_MARKED_AS_PAID;
            case "CLOSED" -> AuditAction.CLAIM_CLOSED;
            default -> AuditAction.CLAIM_UPDATED;
        };
    }
}
