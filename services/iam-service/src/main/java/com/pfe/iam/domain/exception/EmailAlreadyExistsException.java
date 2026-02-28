package com.pfe.iam.domain.exception;

import com.pfe.commons.exceptions.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}
