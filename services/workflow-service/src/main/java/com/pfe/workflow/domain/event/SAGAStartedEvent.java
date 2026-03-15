package com.pfe.workflow.domain.event;

import com.pfe.commons.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class SAGAStartedEvent implements DomainEvent {
    private final String eventId;
    private final UUID sagaId;
    private final String sagaType;
    private final UUID initiatedBy;
    private final LocalDateTime timestamp;

    public SAGAStartedEvent(UUID sagaId, String sagaType, UUID initiatedBy) {
        this.eventId = UUID.randomUUID().toString();
        this.sagaId = sagaId;
        this.sagaType = sagaType;
        this.initiatedBy = initiatedBy;
        this.timestamp = LocalDateTime.now();
    }
}
