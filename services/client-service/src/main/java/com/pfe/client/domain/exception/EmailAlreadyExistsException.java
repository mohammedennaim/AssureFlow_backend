package com.pfe.client.domain.exception;

import com.pfe.commons.exceptions.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " is already registered for another client");
    }
}
