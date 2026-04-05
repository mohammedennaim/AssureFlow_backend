package com.pfe.billing.infrastructure.client;

import com.pfe.commons.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class ClientServiceClientFallback implements ClientServiceClient {

    @Override
    public BaseResponse<ClientDto> getClientById(String identifier) {
        log.warn("[FALLBACK] client-service unavailable, returning error response for client: {}", identifier);
        return BaseResponse.error("Client service is unavailable");
    }
}
