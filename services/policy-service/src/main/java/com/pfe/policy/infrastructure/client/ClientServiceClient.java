package com.pfe.policy.infrastructure.client;

import com.pfe.commons.dto.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "client-service",
        url = "${feign.client.config.client-service.url}",
        fallback = ClientServiceClientFallback.class
)
public interface ClientServiceClient {

    @GetMapping("/api/v1/clients/{identifier}")
    BaseResponse<ClientDto> getClientById(@PathVariable("identifier") String identifier);
}
