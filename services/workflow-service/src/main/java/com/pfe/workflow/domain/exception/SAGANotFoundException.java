package com.pfe.workflow.domain.exception;

import java.util.UUID;

public class SAGANotFoundException extends RuntimeException {

    public SAGANotFoundException(UUID id) {
        super("SAGA transaction with ID " + id + " not found");
    }

    public SAGANotFoundException(String message) {
        super(message);
    }
}
