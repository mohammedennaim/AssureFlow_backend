package com.pfe.iam.infrastructure.config;

import com.pfe.commons.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Génère automatiquement les tokens JWT pour les service accounts au démarrage de l'application.
 * Ces tokens ont une longue durée de vie (10 ans) et sont utilisés pour la communication inter-services.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceAccountTokenGenerator {

    private final JwtService jwtService;

    /**
     * Génère les tokens de service au démarrage de l'application.
     * Les tokens sont affichés dans les logs pour être copiés dans le fichier .env
     */
    @EventListener(ApplicationReadyEvent.class)
    public void generateServiceTokens() {
        log.info("========================================");
        log.info("  Génération des Service Account Tokens");
        log.info("========================================");

        try {
            // Token 1: SERVICE_ACCOUNT_TOKEN (pour les appels inter-services)
            String serviceToken = generateToken(
                "service-account",
                Arrays.asList("ROLE_SERVICE", "ROLE_ADMIN"),
                10 * 365 * 24 * 60 * 60 * 1000L // 10 ans en millisecondes
            );

            log.info("");
            log.info("1. SERVICE_ACCOUNT_TOKEN:");
            log.info("   Subject: service-account");
            log.info("   Roles: ROLE_SERVICE, ROLE_ADMIN");
            log.info("   Validité: 10 ans");
            log.info("   Token: {}", serviceToken);

            // Token 2: FEIGN_CLIENT_JWT_TOKEN (legacy, pour compatibilité)
            String feignToken = generateToken(
                "feign-client-service",
                Arrays.asList("ROLE_SERVICE"),
                10 * 365 * 24 * 60 * 60 * 1000L // 10 ans en millisecondes
            );

            log.info("");
            log.info("2. FEIGN_CLIENT_JWT_TOKEN:");
            log.info("   Subject: feign-client-service");
            log.info("   Roles: ROLE_SERVICE");
            log.info("   Validité: 10 ans");
            log.info("   Token: {}", feignToken);

            log.info("");
            log.info("========================================");
            log.info("  Copier ces lignes dans votre .env");
            log.info("========================================");
            log.info("");
            log.info("SERVICE_ACCOUNT_TOKEN={}", serviceToken);
            log.info("");
            log.info("FEIGN_CLIENT_JWT_TOKEN={}", feignToken);
            log.info("");
            log.info("✓ Tokens générés avec succès!");
            log.info("========================================");

        } catch (Exception e) {
            log.error("Erreur lors de la génération des tokens de service", e);
        }
    }

    /**
     * Génère un token JWT avec les paramètres spécifiés.
     *
     * @param subject Le sujet du token (identifiant du service)
     * @param roles Les rôles à attribuer au token
     * @param expirationMs La durée de validité en millisecondes
     * @return Le token JWT généré
     */
    private String generateToken(String subject, List<String> roles, long expirationMs) {
        List<GrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        UserDetails userDetails = User.builder()
            .username(subject)
            .password("") // Pas de mot de passe pour les service accounts
            .authorities(authorities)
            .build();

        return jwtService.generateToken(userDetails, roles, expirationMs);
    }

    /**
     * Endpoint REST pour générer manuellement les tokens (optionnel).
     * Peut être appelé via: GET /api/v1/auth/generate-service-tokens
     * Nécessite le rôle ADMIN.
     */
    public ServiceTokensResponse generateServiceTokensManually() {
        String serviceToken = generateToken(
            "service-account",
            Arrays.asList("ROLE_SERVICE", "ROLE_ADMIN"),
            10 * 365 * 24 * 60 * 60 * 1000L
        );

        String feignToken = generateToken(
            "feign-client-service",
            Arrays.asList("ROLE_SERVICE"),
            10 * 365 * 24 * 60 * 60 * 1000L
        );

        return new ServiceTokensResponse(serviceToken, feignToken);
    }

    /**
     * DTO pour la réponse de génération manuelle des tokens.
     */
    public record ServiceTokensResponse(
        String serviceAccountToken,
        String feignClientToken
    ) {}
}
