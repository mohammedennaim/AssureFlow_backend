package com.pfe.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Rewrites requests from /swagger-ui/** to /webjars/swagger-ui/** so that
 * the Swagger UI is accessible on a clean path without exposing "/webjars"
 * in the browser URL.
 */
@Component
public class SwaggerUiWebFilter implements WebFilter {

    private static final String SWAGGER_PREFIX = "/swagger-ui";
    private static final String WEBJARS_PREFIX = "/webjars/swagger-ui";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.equals(SWAGGER_PREFIX) || path.equals(SWAGGER_PREFIX + "/")) {
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .path(WEBJARS_PREFIX + "/index.html")
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }

        if (path.startsWith(SWAGGER_PREFIX + "/")) {
            // Strip trailing slash (e.g. /swagger-ui/index.html/ -> /swagger-ui/index.html)
            if (path.endsWith("/") && path.length() > (SWAGGER_PREFIX + "/").length()) {
                path = path.substring(0, path.length() - 1);
            }
            String newPath = "/webjars" + path;
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .path(newPath)
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }

        return chain.filter(exchange);
    }
}
