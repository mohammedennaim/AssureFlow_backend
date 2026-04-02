package com.pfe.iam.infrastructure.web.controller;

import com.pfe.iam.infrastructure.config.ServiceAccountTokenGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour générer manuellement les tokens de service.
 * Accessible uniquement aux administrateurs.
 */
@RestController
@RequestMapping("/api/v1/service-tokens")
@RequiredArgsConstructor
@Tag(name = "Service Tokens", description = "Génération de tokens pour les service accounts")
@SecurityRequirement(name = "bearerAuth")
public class ServiceTokenController {

    private final ServiceAccountTokenGenerator tokenGenerator;

    /**
     * Génère de nouveaux tokens JWT pour les service accounts.
     * Ces tokens ont une validité de 10 ans et sont utilisés pour la communication inter-services.
     *
     * @return Les tokens générés (SERVICE_ACCOUNT_TOKEN et FEIGN_CLIENT_JWT_TOKEN)
     */
    @GetMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Générer les tokens de service",
        description = "Génère de nouveaux tokens JWT pour les service accounts avec une validité de 10 ans. " +
                     "Ces tokens doivent être copiés dans le fichier .env de chaque microservice."
    )
    public ResponseEntity<ServiceAccountTokenGenerator.ServiceTokensResponse> generateTokens() {
        ServiceAccountTokenGenerator.ServiceTokensResponse response = tokenGenerator.generateServiceTokensManually();
        return ResponseEntity.ok(response);
    }
}
