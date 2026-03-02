package com.pfe.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String id;
    private String invoiceId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod method;
    private String reference;
}
