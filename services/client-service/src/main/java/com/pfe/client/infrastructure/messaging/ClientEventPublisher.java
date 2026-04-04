package com.pfe.client.infrastructure.messaging;

import com.pfe.client.domain.event.ClientCreatedEvent;
import com.pfe.client.domain.event.ClientDeletedEvent;
import com.pfe.client.domain.event.ClientUpdatedEvent;
import com.pfe.client.domain.model.Client;
import com.pfe.commons.messaging.AbstractEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Publisher for client-related Kafka events.
 * Publishes client lifecycle events to the "client-events" topic.
 */
@Slf4j
@Component
public class ClientEventPublisher extends AbstractEventPublisher {

    private static final String CLIENT_TOPIC = "client-events";

    public ClientEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate, "client-service");
    }

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

            publish(CLIENT_TOPIC, "client.created", payload);
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

            publish(CLIENT_TOPIC, "client.updated", payload);
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

            publish(CLIENT_TOPIC, "client.deleted", payload);
        } catch (Exception e) {
            log.error("[KAFKA] Error publishing ClientDeletedEvent", e);
        }
    }
}
