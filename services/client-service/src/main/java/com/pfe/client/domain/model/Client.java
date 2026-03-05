package com.pfe.client.domain.model;

import com.pfe.commons.annotations.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AggregateRoot
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @EqualsAndHashCode.Include
    private UUID id;
    private String clientNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String cin;
    private ClientStatus status;
    private ClientType type;
    private UUID userId;
    @Builder.Default
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

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
        if (newClientData.getCin() != null)
            this.cin = newClientData.getCin();
        if (newClientData.getStatus() != null)
            this.status = newClientData.getStatus();
        if (newClientData.getType() != null)
            this.type = newClientData.getType();
        if (newClientData.getUserId() != null)
            this.userId = newClientData.getUserId();
        if (newClientData.isActive() != this.active)
            this.active = newClientData.isActive();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(Client dto) {
        update(dto);
    }

    public boolean isActive() {
        return active;
    }
}
