package com.pfe.billing.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for ClientServiceClient.
 * Returns null when client-service is unavailable.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ClientServiceClientFallback implements ClientServiceClient {

    @Override
    public ClientDto getClientById(String id) {
        log.warn("[FALLBACK] client-service unavailable, returning null for client: {}", id);
        return null;
    }
}
