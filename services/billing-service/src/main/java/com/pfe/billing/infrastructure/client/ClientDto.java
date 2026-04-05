package com.pfe.billing.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String cin;
    private String clientNumber;
}
