package com.pfe.policy.infrastructure.client;

import com.pfe.commons.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientServiceClientFallback implements ClientServiceClient {

    @Override
    public BaseResponse<ClientDto> getClientById(String identifier) {
        log.warn("[FALLBACK] client-service is unavailable. Cannot fetch client {}", identifier);
        return BaseResponse.error("Client service is unavailable");
    }
}
