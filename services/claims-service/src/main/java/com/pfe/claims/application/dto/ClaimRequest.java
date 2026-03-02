package com.pfe.claims.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    @NotBlank(message = "Policy ID is required")
    private String policyId;

    @NotBlank(message = "Client ID is required")
    private String clientId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    @NotNull(message = "Claimed amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Claimed amount must be greater than 0")
    private BigDecimal claimedAmount;
}
