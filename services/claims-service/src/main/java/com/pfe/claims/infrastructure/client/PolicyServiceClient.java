package com.pfe.claims.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class PolicyServiceClient {
    private static final String POLICY_SERVICE_URL = "http://policy-service:8080/api/policies";

    public boolean policyExists(UUID policyId) {
        log.info("[POLICY] Checking if policy {} exists (RestTemplate integration pending)", policyId);
        return true;
    }

    public boolean isPolicyActive(UUID policyId) {
        log.info("[POLICY] Checking if policy {} is active (RestTemplate integration pending)", policyId);
        return true;
    }
}
