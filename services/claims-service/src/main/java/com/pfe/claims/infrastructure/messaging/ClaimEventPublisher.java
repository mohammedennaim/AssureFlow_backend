package com.pfe.claims.infrastructure.messaging;

import com.pfe.claims.domain.model.Claim;
import com.pfe.claims.domain.repository.ClaimRepository;
import com.pfe.claims.infrastructure.client.ClientDto;
import com.pfe.claims.infrastructure.client.ClientServiceClient;
import com.pfe.claims.infrastructure.client.PolicyDto;
import com.pfe.claims.infrastructure.client.PolicyServiceClient;
import com.pfe.commons.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Publisher for claim-related Kafka events.
 * Publishes claim lifecycle events to the "claim-events" topic.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PolicyServiceClient policyServiceClient;
    private final ClientServiceClient clientServiceClient;
    private final ClaimRepository claimRepository;
    private final org.springframework.jdbc.core.JdbcTemplate clientJdbcTemplate;

    /**
     * Get client email directly from client_db (fallback when Feign client fails)
     */
    private String getClientEmailFromDB(UUID clientId) {
        try {
            log.info("[DB] Trying to fetch email for clientId: {}", clientId);
            String sql = "SELECT email FROM clients WHERE id = ?";
            String email = clientJdbcTemplate.queryForObject(sql, String.class, clientId.toString());
            log.info("[DB] Found email for clientId {}: {}", clientId, email);
            return email;
        } catch (Exception e) {
            log.warn("[DB] Could not fetch client email from client_db for {}: {}", clientId, e.getMessage());
        }
        return null;
    }

    /**
     * Publishes a claim created event (claim.submitted).
     */
    public void publishClaimCreated(UUID claimId, UUID policyId, UUID clientId, LocalDateTime createdAt, LocalDateTime slaDeadline) {
        // Fetch policy and client data for notification (best effort - don't fail if unavailable)
        String clientEmail = null;
        String clientPhone = null;
        String claimNumber = null;

        try {
            PolicyDto policy = policyServiceClient.getPolicyById(policyId.toString());
            if (policy != null) {
                claimNumber = policy.getPolicyNumber(); // Use policy number as reference
                // Fetch client data
                try {
                    ClientDto client = extractClient(clientServiceClient.getClientById(policy.getClientId()));
                    if (client != null) {
                        clientEmail = client.getEmail();
                        clientPhone = client.getPhone();
                    }
                } catch (Exception e) {
                    log.warn("[KAFKA] Could not fetch client data for notification: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("[KAFKA] Could not fetch policy data for notification: {}", e.getMessage());
        }

        // Build payload with null-safe handling
        Map<String, Object> payload = new HashMap<>();
        payload.put("claimId", claimId.toString());
        payload.put("claimNumber", claimNumber != null ? claimNumber : "CLM-" + claimId.toString().substring(0, 8).toUpperCase());
        payload.put("policyId", policyId.toString());
        payload.put("clientId", clientId.toString());
        payload.put("clientEmail", clientEmail);
        payload.put("clientPhone", clientPhone);
        payload.put("createdAt", createdAt != null ? createdAt.toString() : LocalDateTime.now().toString());
        payload.put("slaDeadline", slaDeadline != null ? slaDeadline.toString() : null);
        payload.put("status", "SUBMITTED");
        payload.put("timestamp", LocalDateTime.now().toString());

        String eventType = "claim.submitted";
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("claim-events", eventType, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[KAFKA] Failed to publish claim.submitted event for claim {}: {}",
                        claimId, ex.getMessage());
            } else {
                log.info("[KAFKA] claim.submitted published → claim={} topic={} partition={} offset={}",
                        claimId,
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    /**
     * Publishes a claim status change event.
     */
    public void publishClaimStatusChanged(UUID claimId, String oldStatus, String newStatus, UUID userId) {
        try {
            // Map internal status to event type
            String eventType = mapStatusToEventType(newStatus);

            // Fetch claim, policy and client data for notification
            String clientId = null;
            String clientEmail = null;
            String clientPhone = null;
            String claimNumber = null;
            Object approvedAmount = null;

            try {
                // Fetch claim data from database
                var claimOpt = claimRepository.findById(claimId);
                if (claimOpt.isPresent()) {
                    var claim = claimOpt.get();
                    claimNumber = claim.getClaimNumber();
                    clientId = claim.getClientId() != null ? claim.getClientId().toString() : null;
                    
                    try {
                        PolicyDto policy = policyServiceClient.getPolicyById(claim.getPolicyId().toString());
                        if (policy != null && policy.getClientId() != null) {
                            try {
                                ClientDto client = extractClient(
                                        clientServiceClient.getClientById(policy.getClientId().toString()));
                                if (client != null) {
                                    clientEmail = client.getEmail();
                                    clientPhone = client.getPhone();
                                }
                            } catch (Exception e) {
                                log.warn("[KAFKA] Could not fetch client via Feign: {}. Trying DB fallback...", e.getMessage());
                                // Fallback: get client email directly from DB
                                clientEmail = getClientEmailFromDB(UUID.fromString(policy.getClientId().toString()));
                            }
                        }
                    } catch (Exception e) {
                        log.warn("[KAFKA] Could not fetch policy/client data for notification: {}", e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("[KAFKA] Could not fetch claim data: {}", e.getMessage());
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("claimId", claimId.toString());
            payload.put("claimNumber", claimNumber);
            payload.put("clientId", clientId);
            payload.put("clientEmail", clientEmail);
            payload.put("clientPhone", clientPhone);
            payload.put("oldStatus", oldStatus);
            payload.put("newStatus", newStatus);
            payload.put("approvedAmount", approvedAmount);
            payload.put("userId", userId != null ? userId.toString() : null);
            payload.put("timestamp", java.time.LocalDateTime.now().toString());

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send("claim-events", eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish {} event for claim {}: {}",
                            eventType, claimId, ex.getMessage());
                } else {
                    log.info("[KAFKA] {} published → claim={} topic={} partition={} offset={}",
                            eventType, claimId,
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing claim status event", e);
        }
    }

    /**
     * Publishes a claim approved event with amount.
     */
    public void publishClaimApproved(UUID claimId, String claimNumber, String clientId, String clientEmail, 
                                      String clientPhone, Object approvedAmount) {
        publishClaimEvent("claim.approved", claimId, claimNumber, clientId, clientEmail, clientPhone, approvedAmount);
    }

    /**
     * Publishes a claim rejected event.
     */
    public void publishClaimRejected(UUID claimId, String claimNumber, String clientId, String clientEmail,
                                      String clientPhone, String rejectionReason) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("claimId", claimId.toString());
            payload.put("claimNumber", claimNumber);
            payload.put("clientId", clientId);
            payload.put("clientEmail", clientEmail);
            payload.put("clientPhone", clientPhone);
            payload.put("rejectionReason", rejectionReason);
            payload.put("timestamp", LocalDateTime.now().toString());

            String eventType = "claim.rejected";
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send("claim-events", eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish {} event for claim {}: {}",
                            eventType, claimId, ex.getMessage());
                } else {
                    log.info("[KAFKA] {} published → claim={} topic={} partition={} offset={}",
                            eventType, claimId,
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing {} event", "claim.rejected", e);
        }
    }

    /**
     * Publishes a claim paid event.
     */
    public void publishClaimPaid(UUID claimId, String claimNumber, String clientId, String clientEmail,
                                  String clientPhone, Object paidAmount) {
        publishClaimEvent("claim.paid", claimId, claimNumber, clientId, clientEmail, clientPhone, paidAmount);
    }

    /**
     * Publishes a claim SLA breached event.
     */
    public void publishClaimSlaBreached(UUID claimId, String claimNumber, String clientId, String clientEmail,
                                         String clientPhone) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("claimId", claimId.toString());
            payload.put("claimNumber", claimNumber);
            payload.put("clientId", clientId);
            payload.put("clientEmail", clientEmail);
            payload.put("clientPhone", clientPhone);
            payload.put("slaBreached", true);
            payload.put("timestamp", LocalDateTime.now().toString());

            String eventType = "claim.sla.breached";
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send("claim-events", eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish {} event for claim {}: {}",
                            eventType, claimId, ex.getMessage());
                } else {
                    log.info("[KAFKA] {} published → claim={} topic={} partition={} offset={}",
                            eventType, claimId,
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing {} event", "claim.sla.breached", e);
        }
    }

    private void publishClaimEvent(String eventType, UUID claimId, String claimNumber, String clientId,
                                    String clientEmail, String clientPhone, Object amount) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("claimId", claimId.toString());
            payload.put("claimNumber", claimNumber);
            payload.put("clientId", clientId);
            payload.put("clientEmail", clientEmail);
            payload.put("clientPhone", clientPhone);
            if (amount != null) {
                payload.put("approvedAmount", amount);
                payload.put("paidAmount", amount);
            }
            payload.put("timestamp", LocalDateTime.now().toString());

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send("claim-events", eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish {} event for claim {}: {}",
                            eventType, claimId, ex.getMessage());
                } else {
                    log.info("[KAFKA] {} published → claim={} topic={} partition={} offset={}",
                            eventType, claimId,
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing {} event", eventType, e);
        }
    }

    private String mapStatusToEventType(String status) {
        return switch (status) {
            case "APPROVED" -> "claim.approved";
            case "REJECTED" -> "claim.rejected";
            case "PAID" -> "claim.paid";
            case "SUBMITTED" -> "claim.submitted";
            case "UNDER_REVIEW" -> "claim.under_review";
            case "INFO_REQUESTED" -> "claim.info_requested";
            case "CLOSED" -> "claim.closed";
            default -> "claim.updated";
        };
    }

    private ClientDto extractClient(BaseResponse<ClientDto> response) {
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return response.getData();
    }
}
