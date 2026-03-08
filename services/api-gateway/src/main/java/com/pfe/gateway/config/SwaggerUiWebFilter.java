package com.pfe.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class SwaggerUiWebFilter implements WebFilter {

    private static final String SWAGGER_PREFIX = "/swagger-ui";
    private static final String REDIRECT_URL = "/swagger-ui/index.html";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.equals(SWAGGER_PREFIX)
                || path.equals(SWAGGER_PREFIX + "/")
                || path.equals(SWAGGER_PREFIX + "/index.html")) {
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create(REDIRECT_URL));
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
