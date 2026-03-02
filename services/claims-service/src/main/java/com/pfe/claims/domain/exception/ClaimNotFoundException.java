package com.pfe.claims.domain.exception;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(String id) {
        super("Claim not found with id: " + id);
    }
}
