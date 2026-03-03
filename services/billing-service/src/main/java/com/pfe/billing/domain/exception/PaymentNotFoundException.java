package com.pfe.billing.domain.exception;

import com.pfe.commons.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class PaymentNotFoundException extends ResourceNotFoundException {
    public PaymentNotFoundException(UUID id) {
        super("Payment", "id", id);
    }
}
