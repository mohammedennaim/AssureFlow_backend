package com.pfe.client.infrastructure.messaging;

import com.pfe.client.domain.event.ClientCreatedEvent;
import com.pfe.client.domain.event.ClientDeletedEvent;
import com.pfe.client.domain.event.ClientUpdatedEvent;
import com.pfe.client.domain.model.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Publisher for client-related Kafka events.
 * Publishes client lifecycle events to the "client-events" topic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventPublisher {

    private static final String CLIENT_TOPIC = "client-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes a client created event to Kafka.
     */
    public void publishClientCreated(ClientCreatedEvent event) {
        try {
            Client client = event.getClient();
            Map<String, Object> payload = new HashMap<>();
            payload.put("clientId", event.getClientId() != null ? event.getClientId().toString() : null);
            payload.put("email", client.getEmail());
            payload.put("phone", client.getPhone());
            payload.put("firstName", client.getFirstName());
            payload.put("lastName", client.getLastName());
            payload.put("clientNumber", client.getClientNumber());
            payload.put("cin", client.getCin());
            payload.put("timestamp", java.time.LocalDateTime.now().toString());

            String eventType = "client.created";
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(CLIENT_TOPIC, eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish ClientCreatedEvent for client {}: {}",
                            event.getClientId(), ex.getMessage());
                } else {
                    log.info("[KAFKA] ClientCreatedEvent published → client={} topic={} partition={} offset={}",
                            event.getClientId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing ClientCreatedEvent", e);
        }
    }

    /**
     * Publishes a client updated event to Kafka.
     */
    public void publishClientUpdated(ClientUpdatedEvent event) {
        try {
            Client client = event.getClient();
            Map<String, Object> payload = new HashMap<>();
            payload.put("clientId", event.getClientId() != null ? event.getClientId().toString() : null);
            payload.put("email", client.getEmail());
            payload.put("phone", client.getPhone());
            payload.put("firstName", client.getFirstName());
            payload.put("lastName", client.getLastName());
            payload.put("updatedFields", "profile");
            payload.put("timestamp", java.time.LocalDateTime.now().toString());

            String eventType = "client.updated";
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(CLIENT_TOPIC, eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish ClientUpdatedEvent for client {}: {}",
                            event.getClientId(), ex.getMessage());
                } else {
                    log.info("[KAFKA] ClientUpdatedEvent published → client={} topic={} partition={} offset={}",
                            event.getClientId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing ClientUpdatedEvent", e);
        }
    }

    /**
     * Publishes a client deleted event to Kafka.
     */
    public void publishClientDeleted(ClientDeletedEvent event) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("clientId", event.getClientId() != null ? event.getClientId().toString() : null);
            payload.put("deletionReason", "user_request");
            payload.put("timestamp", java.time.LocalDateTime.now().toString());

            String eventType = "client.deleted";
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(CLIENT_TOPIC, eventType, payload);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA] Failed to publish ClientDeletedEvent for client {}: {}",
                            event.getClientId(), ex.getMessage());
                } else {
                    log.info("[KAFKA] ClientDeletedEvent published → client={} topic={} partition={} offset={}",
                            event.getClientId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing ClientDeletedEvent", e);
        }
    }
}
