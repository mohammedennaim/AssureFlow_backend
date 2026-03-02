package com.pfe.iam.domain.exception;

import com.pfe.commons.exceptions.BusinessException;

public class SessionExpiredException extends BusinessException {
    public SessionExpiredException(String sessionId) {
        super(String.format("Session %s has expired", sessionId));
    }
}
