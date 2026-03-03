package com.pfe.billing.domain.exception;

import com.pfe.commons.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class InvoiceNotFoundException extends ResourceNotFoundException {
    public InvoiceNotFoundException(UUID id) {
        super("Invoice", "id", id);
    }

    public InvoiceNotFoundException(String invoiceNumber) {
        super("Invoice", "invoiceNumber", invoiceNumber);
    }
}
