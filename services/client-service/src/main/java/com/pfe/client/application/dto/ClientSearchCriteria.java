package com.pfe.client.application.dto;

import com.pfe.client.domain.model.ClientStatus;
import com.pfe.client.domain.model.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSearchCriteria {
    private String firstName;
    private String lastName;
    private String email;
    private String cin;
    private ClientStatus status;
    private ClientType type;
    private LocalDate dateOfBirthFrom;
    private LocalDate dateOfBirthTo;
}
