package com.pfe.client.domain.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String id) {
        super("Client with ID " + id + " not found");
    }
}
