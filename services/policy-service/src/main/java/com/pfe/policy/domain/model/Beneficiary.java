package com.pfe.policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {
    private String id;
    private String policyId;
    private String name;
    private String relationship;
    private Double percentage;
}
