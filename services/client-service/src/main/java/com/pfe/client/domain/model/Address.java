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
public class Address {
    private UUID id;
    private UUID clientId;
    private String street;
    private String city;
    private String postalCode;
    private String country;
    private boolean primary;
}
