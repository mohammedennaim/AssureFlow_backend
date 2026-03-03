package com.pfe.client.domain.event;

import com.pfe.client.domain.model.Client;
import com.pfe.commons.events.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClientCreatedEvent extends BaseEvent {
    private UUID clientId;
    private Client client;
}
