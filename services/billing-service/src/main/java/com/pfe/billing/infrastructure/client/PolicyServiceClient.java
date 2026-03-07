package com.pfe.billing.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "policy-service", url = "${feign.client.config.policy-service.url}", fallback = PolicyServiceClientFallback.class)
public interface PolicyServiceClient {

    @GetMapping("/api/v1/policies/{id}")
    @CircuitBreaker(name = "policy-service")
    @Retry(name = "policy-service")
    PolicyDto getPolicyById(@PathVariable("id") String id);
}
