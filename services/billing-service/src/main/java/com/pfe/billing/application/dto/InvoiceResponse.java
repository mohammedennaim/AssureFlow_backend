package com.pfe.billing.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private String id;
    private String invoiceNumber;
    private String policyId;
    private String clientId;
    private BigDecimal amount;
    private BigDecimal remainingBalance;
    private LocalDate dueDate;
    private String status;
    private List<PaymentResponse> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
