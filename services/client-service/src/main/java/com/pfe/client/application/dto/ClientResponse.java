package com.pfe.client.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.pfe.client.domain.model.ClientStatus;
import com.pfe.client.domain.model.ClientType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private String id;
    private String clientNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String cin;
    private List<AddressDto> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ClientStatus status;
    private ClientType type;
    private String userId;
}
