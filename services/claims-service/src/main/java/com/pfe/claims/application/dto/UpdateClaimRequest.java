package com.pfe.claims.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClaimRequest {
    private String description;
    private LocalDate incidentDate;
    private BigDecimal estimatedAmount;
    private UUID assignedTo;
}
