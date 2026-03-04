package com.pfe.notification.domain.exception;

import com.pfe.commons.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class NotificationNotFoundException extends ResourceNotFoundException {
    public NotificationNotFoundException(UUID id) {
        super("Notification", "id", id);
    }
}
