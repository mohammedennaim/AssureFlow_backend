package com.pfe.client.domain.exception;

import com.pfe.commons.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class ClientNotFoundException extends ResourceNotFoundException {
    public ClientNotFoundException(UUID id) {
        super("Client", "id", id);
    }
}
