package com.pfe.claims.domain.event;

import com.pfe.commons.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClaimSubmittedEvent extends BaseEvent {
    private UUID claimId;
    private String claimNumber;
    private UUID policyId;
    private UUID clientId;
    private String status;
    private LocalDate incidentDate;
    private BigDecimal estimatedAmount;
    private String description;
}
