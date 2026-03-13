package com.pfe.gateway.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/clients")
    public ResponseEntity<Map<String, Object>> clientServiceFallback() {
        log.warn("Client service is currently unavailable - returning fallback response");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Client service temporarily unavailable",
                        "message", "Please try again later",
                        "timestamp", LocalDateTime.now(),
                        "service", "client-service"
                ));
    }

    @GetMapping("/policies")
    public ResponseEntity<Map<String, Object>> policyServiceFallback() {
        log.warn("Policy service is currently unavailable - returning fallback response");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Policy service temporarily unavailable",
                        "message", "Please try again later",
                        "timestamp", LocalDateTime.now(),
                        "service", "policy-service"
                ));
    }

    @GetMapping("/claims")
    public ResponseEntity<Map<String, Object>> claimsServiceFallback() {
        log.warn("Claims service is currently unavailable - returning fallback response");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Claims service temporarily unavailable",
                        "message", "Please try again later",
                        "timestamp", LocalDateTime.now(),
                        "service", "claims-service"
                ));
    }

    @GetMapping("/billing")
    public ResponseEntity<Map<String, Object>> billingServiceFallback() {
        log.warn("Billing service is currently unavailable - returning fallback response");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Billing service temporarily unavailable",
                        "message", "Please try again later",
                        "timestamp", LocalDateTime.now(),
                        "service", "billing-service"
                ));
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        log.warn("Notification service is currently unavailable - returning fallback response");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Notification service temporarily unavailable",
                        "message", "Please try again later",
                        "timestamp", LocalDateTime.now(),
                        "service", "notification-service"
                ));
    }
}