package com.pfe.claims.application.dto;

import com.pfe.claims.domain.model.PaymentStatus;
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
public class ClaimPayoutDto {
    private UUID id;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private UUID payedBy;
    private UUID authorizedBy;
}
