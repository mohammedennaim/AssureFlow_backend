package com.pfe.commons.events;

import java.time.LocalDateTime;

public interface DomainEvent {
    String getEventId();

    LocalDateTime getTimestamp();
}
