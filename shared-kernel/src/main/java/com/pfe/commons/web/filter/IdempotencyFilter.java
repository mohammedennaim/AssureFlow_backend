package com.pfe.commons.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
    private static final Set<String> IDEMPOTENT_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private static final ConcurrentHashMap<String, Long> processedRequests = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER);

        if (idempotencyKey == null || !IDEMPOTENT_METHODS.contains(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (processedRequests.containsKey(idempotencyKey)) {
            log.warn("[IDEMPOTENCY] Duplicate request detected for key={} method={} uri={}",
                    idempotencyKey, request.getMethod(), request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("X-Idempotency-Replayed", "true");
            response.getWriter().write("{\"message\":\"Duplicate request — already processed\"}");
            return;
        }

        processedRequests.put(idempotencyKey, System.currentTimeMillis());
        log.debug("[IDEMPOTENCY] New request registered key={}", idempotencyKey);

        filterChain.doFilter(request, response);
    }
}
