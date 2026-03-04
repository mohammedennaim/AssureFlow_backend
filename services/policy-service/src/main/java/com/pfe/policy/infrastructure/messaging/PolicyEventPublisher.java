package com.pfe.policy.infrastructure.messaging;

import com.pfe.policy.domain.event.PolicyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PolicyEventPublisher {
    private static final String POLICY_TOPIC = "policy-events";

    public void publishPolicyCreated(PolicyCreatedEvent event) {
        log.info("[EVENT] PolicyCreatedEvent for policy: {} (Kafka integration pending)", event.getPolicyId());
    }

    public void publishPolicyEvent(String eventType, Object event) {
        log.info("[EVENT] {} event published (Kafka integration pending)", eventType);
    }
}
