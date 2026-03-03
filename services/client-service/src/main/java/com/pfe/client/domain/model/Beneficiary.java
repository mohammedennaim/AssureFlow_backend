package com.pfe.client.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {
    private UUID id;
    private UUID clientId;
    private String firstName;
    private String lastName;
    private String relationship;
    private String phone;
    private String email;
    private Integer percentage;
}
