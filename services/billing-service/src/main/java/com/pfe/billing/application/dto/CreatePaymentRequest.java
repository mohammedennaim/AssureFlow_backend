package com.pfe.billing.application.dto;

import com.pfe.billing.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
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
public class CreatePaymentRequest {

    @NotNull
    private UUID invoiceId;

    @NotNull
    private UUID clientId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;

    private String transactionId;
    private UUID processedBy;
}
