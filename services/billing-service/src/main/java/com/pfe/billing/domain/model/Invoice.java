package com.pfe.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    private String id;
    private String invoiceNumber;
    private String policyId;
    private String clientId;
    private BigDecimal amount;
    private LocalDate dueDate;
    private InvoiceStatus status;
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BigDecimal getRemainingBalance() {
        if (payments == null || payments.isEmpty()) return amount;
        BigDecimal paid = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return amount.subtract(paid);
    }

    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
    }

    public void markAsOverdue() {
        this.status = InvoiceStatus.OVERDUE;
    }
}
