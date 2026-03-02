package com.pfe.claims.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApprovalRequest {
    @NotNull(message = "Approved amount is required")
    private BigDecimal approvedAmount;
}
