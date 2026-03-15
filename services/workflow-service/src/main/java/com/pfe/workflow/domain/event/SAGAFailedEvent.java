package com.pfe.workflow.domain.event;

import com.pfe.commons.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class SAGAFailedEvent implements DomainEvent {
    private final String eventId;
    private final UUID sagaId;
    private final String sagaType;
    private final String reason;
    private final String failedService;
    private final LocalDateTime timestamp;

    public SAGAFailedEvent(UUID sagaId, String sagaType, String reason, String failedService) {
        this.eventId = UUID.randomUUID().toString();
        this.sagaId = sagaId;
        this.sagaType = sagaType;
        this.reason = reason;
        this.failedService = failedService;
        this.timestamp = LocalDateTime.now();
    }
}
