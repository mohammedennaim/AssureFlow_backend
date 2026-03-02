package com.pfe.iam.domain.exception;

import com.pfe.commons.exceptions.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(String identifier) {
        super(String.format("Role not found: %s", identifier));
    }
}
