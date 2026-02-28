package com.pfe.client.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Address address;
    private String cin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(Client newClientData) {
        if (newClientData.getFirstName() != null)
            this.firstName = newClientData.getFirstName();
        if (newClientData.getLastName() != null)
            this.lastName = newClientData.getLastName();
        if (newClientData.getEmail() != null)
            this.email = newClientData.getEmail();
        if (newClientData.getPhone() != null)
            this.phone = newClientData.getPhone();
        if (newClientData.getDateOfBirth() != null)
            this.dateOfBirth = newClientData.getDateOfBirth();
        if (newClientData.getAddress() != null)
            this.address = newClientData.getAddress();
        if (newClientData.getCin() != null)
            this.cin = newClientData.getCin();
        this.updatedAt = LocalDateTime.now();
    }
}
