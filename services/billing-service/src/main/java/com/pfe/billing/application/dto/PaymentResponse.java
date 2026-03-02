package com.pfe.billing.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String invoiceId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String method;
    private String reference;
}
