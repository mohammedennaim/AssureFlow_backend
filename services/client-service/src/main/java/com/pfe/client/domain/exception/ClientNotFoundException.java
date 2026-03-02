package com.pfe.client.domain.exception;

import com.pfe.commons.exceptions.ResourceNotFoundException;

public class ClientNotFoundException extends ResourceNotFoundException {
    public ClientNotFoundException(String id) {
        super("Client", "id", id);
    }
}
