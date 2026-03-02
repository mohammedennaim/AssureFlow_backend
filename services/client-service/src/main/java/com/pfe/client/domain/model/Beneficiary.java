package com.pfe.client.domain.model;

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
    private String clientId;
    private String firstName;
    private String lastName;
    private String relationship;
    private String phone;
    private String email;
    private Integer percentage;
}
