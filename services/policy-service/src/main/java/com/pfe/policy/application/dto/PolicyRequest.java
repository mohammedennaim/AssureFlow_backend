package com.pfe.policy.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequest {
    @NotBlank(message = "Client ID is required")
    private String clientId;

    @NotNull(message = "Policy type is required")
    private String type;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Premium is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Premium must be greater than 0")
    private BigDecimal premium;

    private String description;

    @Builder.Default
    private List<CoverageRequest> coverages = new ArrayList<>();
}
