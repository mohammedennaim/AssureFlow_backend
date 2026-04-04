package com.pfe.commons.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractEventPublisher {

    protected final KafkaTemplate<String, Object> kafkaTemplate;
    protected final String serviceName;

    protected AbstractEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, String serviceName) {
        this.kafkaTemplate = kafkaTemplate;
        this.serviceName = serviceName;
    }

    protected void publish(String topic, String eventType, Map<String, Object> payload) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, eventType, payload);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[{}] Failed to publish event {} to topic {}: {}",
                    serviceName, eventType, topic, ex.getMessage());
            } else {
                log.debug("[{}] Event {} published to topic {} partition={} offset={}",
                    serviceName, eventType, topic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            }
        });
    }

    protected Map<String, Object> buildBasePayload(String entityId, String entityType) {
        return Map.of(
            "entityId", entityId,
            "entityType", entityType,
            "timestamp", java.time.LocalDateTime.now().toString()
        );
    }
}
