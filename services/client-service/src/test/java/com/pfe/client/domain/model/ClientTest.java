package com.pfe.client.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    @DisplayName("Should return full name correctly")
    void shouldReturnFullName() {
        Client client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        assertEquals("John Doe", client.getFullName());
    }

    @Test
    @DisplayName("Should update client fields")
    void shouldUpdateClientFields() {
        Client existingClient = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        Client newData = Client.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .build();

        existingClient.update(newData);

        assertEquals("Jane", existingClient.getFirstName());
        assertEquals("Smith", existingClient.getLastName());
        assertEquals("jane@example.com", existingClient.getEmail());
        assertNotNull(existingClient.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return active status correctly")
    void shouldReturnActiveStatus() {
        Client activeClient = Client.builder()
                .active(true)
                .build();

        assertTrue(activeClient.isActive());
    }

    @Test
    @DisplayName("Should build client with builder")
    void shouldBuildClientWithBuilder() {
        UUID id = UUID.randomUUID();
        LocalDate dob = LocalDate.of(1990, 1, 1);

        Client client = Client.builder()
                .id(id)
                .clientNumber("CLT-00001")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+212600000000")
                .cin("AB123456")
                .dateOfBirth(dob)
                .status(ClientStatus.ACTIVE)
                .active(true)
                .build();

        assertNotNull(client);
        assertEquals(id, client.getId());
        assertEquals("John", client.getFirstName());
        assertTrue(client.isActive());
    }
}
