package com.pfe.policy.domain.exception;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException(String id) {
        super("Policy not found with id: " + id);
    }
}
