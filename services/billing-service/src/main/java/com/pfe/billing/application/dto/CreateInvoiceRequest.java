package com.pfe.billing.application.dto;

import jakarta.validation.constraints.NotNull;
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
public class CreateInvoiceRequest {

    @NotNull
    private UUID policyId;

    @NotNull
    private UUID clientId;

    @NotNull
    private BigDecimal amount;

    private BigDecimal taxAmount;

    @NotNull
    private LocalDate dueDate;

    private UUID generatedBy;
}
