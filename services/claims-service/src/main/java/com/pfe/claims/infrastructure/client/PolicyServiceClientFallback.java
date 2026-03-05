package com.pfe.claims.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PolicyServiceClientFallback implements PolicyServiceClient {

    @Override
    public PolicyDto getPolicyById(String id) {
        log.warn("[FALLBACK] policy-service is unavailable. Cannot fetch policy {}", id);
        return null;
    }
}
