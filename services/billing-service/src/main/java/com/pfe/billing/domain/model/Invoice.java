package com.pfe.billing.domain.model;

import com.pfe.commons.annotations.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AggregateRoot
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    private UUID id;
    private String invoiceNumber;
    private UUID policyId;
    private UUID clientId;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private UUID generatedBy;
    @Builder.Default
    private boolean paidDirect = false;

    // Diagram methods
    public void markAsPaid(Payment payment) {
        this.status = InvoiceStatus.ACTIVE;
    }

    public void markAsPaidDirect(Payment payment) {
        this.paidDirect = true;
        this.status = InvoiceStatus.ACTIVE;
    }

    public boolean isOverDue() {
        return dueDate != null
                && dueDate.isBefore(LocalDate.now())
                && status != InvoiceStatus.ACTIVE;
    }

    public void cancel() {
        this.status = InvoiceStatus.CANCELLED;
    }
}
