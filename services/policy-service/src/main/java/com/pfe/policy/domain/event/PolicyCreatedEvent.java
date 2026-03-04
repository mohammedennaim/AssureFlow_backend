package com.pfe.policy.domain.event;

import com.pfe.commons.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PolicyCreatedEvent extends BaseEvent {
    private String policyId;
    private String policyNumber;
    private String clientId;
    private String type;
    private String status;
    private BigDecimal premiumAmount;
    private BigDecimal coverageAmount;
    private LocalDate startDate;
    private LocalDate endDate;
}
