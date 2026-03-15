package com.pfe.workflow.domain.event;

import com.pfe.commons.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class SAGACompletedEvent implements DomainEvent {
    private final String eventId;
    private final UUID sagaId;
    private final String sagaType;
    private final LocalDateTime timestamp;

    public SAGACompletedEvent(UUID sagaId, String sagaType) {
        this.eventId = UUID.randomUUID().toString();
        this.sagaId = sagaId;
        this.sagaType = sagaType;
        this.timestamp = LocalDateTime.now();
    }
}
