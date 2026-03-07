package com.pfe.commons.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
    private static final String REDIS_KEY_PREFIX = "idempotency:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);
    private static final Set<String> IDEMPOTENT_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private final StringRedisTemplate redisTemplate;

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

        String redisKey = REDIS_KEY_PREFIX + idempotencyKey;

        Boolean alreadyProcessed = redisTemplate.hasKey(redisKey);

        if (Boolean.TRUE.equals(alreadyProcessed)) {
            log.warn("[IDEMPOTENCY] Duplicate request detected for key={} method={} uri={}",
                    idempotencyKey, request.getMethod(), request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("X-Idempotency-Replayed", "true");
            response.getWriter().write("{\"message\":\"Duplicate request — already processed\"}");
            return;
        }

        redisTemplate.opsForValue().set(redisKey, "processed", IDEMPOTENCY_TTL);
        log.debug("[IDEMPOTENCY] New request registered key={}", idempotencyKey);

        filterChain.doFilter(request, response);
    }
}
