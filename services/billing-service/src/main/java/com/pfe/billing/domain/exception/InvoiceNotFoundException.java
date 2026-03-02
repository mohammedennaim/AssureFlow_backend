package com.pfe.billing.domain.exception;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(String id) {
        super("Invoice not found with id: " + id);
    }
}
