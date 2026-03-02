package com.pfe.policy.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDto {
    private String id;
    private String name;
    private String relationship;
    private Double percentage;
}
