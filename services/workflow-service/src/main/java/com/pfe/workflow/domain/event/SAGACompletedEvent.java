package com.pfe.workflow.domain.event;

import com.pfe.commons.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SAGACompletedEvent extends BaseEvent {
    private UUID sagaId;
    private String sagaType;
}
