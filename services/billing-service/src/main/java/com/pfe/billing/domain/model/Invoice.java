package com.pfe.billing.domain.model;

import com.pfe.commons.annotations.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AggregateRoot
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @EqualsAndHashCode.Include
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

    /**
     * Marks this invoice as paid following a successful payment.
     *
     * @param payment the payment that settled the invoice
     * @throws IllegalStateException if the invoice is already PAID or CANCELLED
     */
    public void markAsPaid(Payment payment) {
        if (this.status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already paid.");
        }
        if (this.status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay a cancelled invoice.");
        }
        this.status = InvoiceStatus.PAID;
    }

    /**
     * Marks this invoice as paid via direct payment channel.
     *
     * @param payment the payment record
     * @throws IllegalStateException if the invoice is already PAID or CANCELLED
     */
    public void markAsPaidDirect(Payment payment) {
        if (this.status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already paid.");
        }
        if (this.status == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay a cancelled invoice.");
        }
        this.paidDirect = true;
        this.status = InvoiceStatus.PAID;
    }

    /**
     * Returns true if this invoice is past its due date and not yet paid.
     */
    public boolean isOverDue() {
        return dueDate != null
                && dueDate.isBefore(LocalDate.now())
                && status != InvoiceStatus.PAID
                && status != InvoiceStatus.CANCELLED;
    }

    /**
     * Cancels this invoice.
     *
     * @throws IllegalStateException if the invoice is already PAID
     */
    public void cancel() {
        if (this.status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a paid invoice.");
        }
        this.status = InvoiceStatus.CANCELLED;
    }
}
