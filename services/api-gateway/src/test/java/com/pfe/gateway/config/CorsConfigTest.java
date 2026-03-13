package com.pfe.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Tests for CORS configuration
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CorsConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Should allow CORS preflight request from allowed origin")
    void shouldAllowCorsPreflightFromAllowedOrigin() {
        webTestClient.options()
                .uri("/api/v1/auth/login")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type,Authorization")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:4200")
                .expectHeader().valueEquals("Access-Control-Allow-Credentials", "true")
                .expectHeader().exists("Access-Control-Allow-Methods")
                .expectHeader().exists("Access-Control-Allow-Headers")
                .expectHeader().exists("Access-Control-Max-Age");
    }

    @Test
    @DisplayName("Should reject CORS request from disallowed origin")
    void shouldRejectCorsFromDisallowedOrigin() {
        webTestClient.options()
                .uri("/api/v1/auth/login")
                .header("Origin", "http://malicious-site.com")
                .header("Access-Control-Request-Method", "POST")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("Should allow CORS request with wildcard pattern")
    void shouldAllowCorsWithWildcardPattern() {
        webTestClient.options()
                .uri("/api/v1/policies")
                .header("Origin", "http://localhost:3001")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3001")
                .expectHeader().valueEquals("Access-Control-Allow-Credentials", "true");
    }

    @Test
    @DisplayName("Should include security headers in response")
    void shouldIncludeSecurityHeaders() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("X-Frame-Options", "DENY")
                .expectHeader().valueEquals("X-XSS-Protection", "1; mode=block")
                .expectHeader().exists("Content-Security-Policy")
                .expectHeader().exists("Referrer-Policy");
    }

    @Test
    @DisplayName("Should set cache control headers for sensitive endpoints")
    void shouldSetCacheControlForSensitiveEndpoints() {
        webTestClient.get()
                .uri("/api/v1/auth/profile")
                .exchange()
                .expectHeader().valueEquals("Cache-Control", "no-cache, no-store, must-revalidate")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Expires", "0");
    }

    @Test
    @DisplayName("Should preserve correlation ID in response headers")
    void shouldPreserveCorrelationIdInResponse() {
        String correlationId = "test-correlation-123";
        
        webTestClient.get()
                .uri("/actuator/health")
                .header("X-Correlation-ID", correlationId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Correlation-ID", correlationId);
    }
}