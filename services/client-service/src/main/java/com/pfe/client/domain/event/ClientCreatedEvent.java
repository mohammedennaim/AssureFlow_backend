package com.pfe.client.domain.event;

import com.pfe.client.domain.model.Client;
import com.pfe.commons.events.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClientCreatedEvent extends BaseEvent {
    private String clientId;
    private Client client;
}
