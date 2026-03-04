package com.pfe.claims.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payout {
    private UUID id;
    private UUID claimId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private UUID payedBy;
    private UUID authorizedBy;
}
