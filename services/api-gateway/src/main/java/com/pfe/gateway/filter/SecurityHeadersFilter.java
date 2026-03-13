package com.pfe.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            
            // Security headers
            response.getHeaders().add("X-Content-Type-Options", "nosniff");
            response.getHeaders().add("X-Frame-Options", "DENY");
            response.getHeaders().add("X-XSS-Protection", "1; mode=block");
            response.getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
            response.getHeaders().add("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
            
            // Content Security Policy (CSP)
            response.getHeaders().add("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "font-src 'self' data:; " +
                "connect-src 'self' ws: wss:; " +
                "frame-ancestors 'none';"
            );
            
            // HSTS (HTTP Strict Transport Security) - only for HTTPS
            if (exchange.getRequest().getURI().getScheme().equals("https")) {
                response.getHeaders().add("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains; preload");
            }
            
            // Cache control for sensitive endpoints
            String path = exchange.getRequest().getPath().value();
            if (path.contains("/auth/") || path.contains("/users/") || path.contains("/admin/")) {
                response.getHeaders().add("Cache-Control", "no-cache, no-store, must-revalidate");
                response.getHeaders().add("Pragma", "no-cache");
                response.getHeaders().add("Expires", "0");
            }
            
            // Add correlation ID for tracing
            String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");
            if (correlationId != null) {
                response.getHeaders().add("X-Correlation-ID", correlationId);
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}