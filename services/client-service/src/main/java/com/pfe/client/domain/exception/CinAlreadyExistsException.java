package com.pfe.client.domain.exception;

import com.pfe.commons.exceptions.BusinessException;

public class CinAlreadyExistsException extends BusinessException {
    public CinAlreadyExistsException(String cin) {
        super("CIN " + cin + " is already registered for another client");
    }
}
