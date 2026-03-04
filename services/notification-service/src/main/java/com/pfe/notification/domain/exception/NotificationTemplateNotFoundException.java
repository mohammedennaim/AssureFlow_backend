package com.pfe.notification.domain.exception;

import com.pfe.commons.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class NotificationTemplateNotFoundException extends ResourceNotFoundException {
    public NotificationTemplateNotFoundException(UUID id) {
        super("NotificationTemplate", "id", id);
    }

    public NotificationTemplateNotFoundException(String name) {
        super("NotificationTemplate", "name", name);
    }
}
