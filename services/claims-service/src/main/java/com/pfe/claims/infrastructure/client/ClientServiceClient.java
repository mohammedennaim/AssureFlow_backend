package com.pfe.claims.infrastructure.client;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.claims.infrastructure.config.FeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for calling client-service.
 */
@FeignClient(name = "client-service", url = "${feign.client.config.client-service.url:http://localhost:8084}", configuration = FeignConfig.class, fallback = ClientServiceClientFallback.class)
public interface ClientServiceClient {

    @GetMapping("/api/v1/clients/{identifier}")
    @CircuitBreaker(name = "client-service")
    @Retry(name = "client-service")
    BaseResponse<ClientDto> getClientById(@PathVariable("identifier") String identifier);
}
