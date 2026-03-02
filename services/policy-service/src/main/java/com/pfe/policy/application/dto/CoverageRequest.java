package com.pfe.policy.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverageRequest {
    @NotBlank(message = "Coverage type is required")
    private String type;

    @NotNull(message = "Coverage limit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Coverage limit must be greater than 0")
    private BigDecimal coverageLimit;

    private String description;
}
