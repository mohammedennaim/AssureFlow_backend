package com.pfe.billing.domain.model;

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
public class PaymentSchedule {

    private UUID id;
    private UUID policyId;
    private PaymentFrequency frequency;
    private LocalDate nextDueDate;
    private BigDecimal amount;
}
