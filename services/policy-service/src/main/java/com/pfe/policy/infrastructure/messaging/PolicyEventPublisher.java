package com.pfe.policy.infrastructure.messaging;

import com.pfe.commons.messaging.AbstractEventPublisher;
import com.pfe.policy.domain.event.PolicyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PolicyEventPublisher extends AbstractEventPublisher {

    private static final String POLICY_TOPIC = "policy-events";

    public PolicyEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate, "policy-service");
    }

    public void publishPolicyCreated(PolicyCreatedEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("policyId", event.getPolicyId());
        payload.put("policyNumber", event.getPolicyNumber());
        payload.put("clientId", event.getClientId());
        payload.put("clientEmail", event.getClientEmail());
        payload.put("clientPhone", event.getClientPhone());
        payload.put("type", event.getType());
        payload.put("status", event.getStatus());
        payload.put("premiumAmount", event.getPremiumAmount());
        payload.put("coverageAmount", event.getCoverageAmount());
        payload.put("startDate", event.getStartDate());
        payload.put("endDate", event.getEndDate());
        payload.put("timestamp", java.time.LocalDateTime.now().toString());

        publish(POLICY_TOPIC, "policy.created", payload);
    }

    public void publishPolicyEvent(String eventType, Object event) {
        publish(POLICY_TOPIC, eventType, Map.of("event", event));
    }
}
