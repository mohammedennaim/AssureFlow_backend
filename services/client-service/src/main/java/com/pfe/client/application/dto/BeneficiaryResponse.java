package com.pfe.client.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String relationship;
    private String phone;
    private String email;
    private Integer percentage;
}
