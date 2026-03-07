package com.pfe.client.application.service.impl;

import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;
import com.pfe.client.application.mapper.ClientMapper;
import com.pfe.client.application.service.ClientHistoryService;
import com.pfe.client.domain.exception.ClientNotFoundException;
import com.pfe.client.domain.exception.CinAlreadyExistsException;
import com.pfe.client.domain.exception.EmailAlreadyExistsException;
import com.pfe.client.domain.model.Client;
import com.pfe.client.domain.model.ClientStatus;
import com.pfe.client.domain.repository.AddressRepository;
import com.pfe.client.domain.repository.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ClientMapper mapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private ClientHistoryService historyService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private final UUID CLIENT_ID = UUID.randomUUID();

    private Client createTestClient() {
        return Client.builder()
                .id(CLIENT_ID)
                .clientNumber("CLT-00001")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+212600000000")
                .cin("AB123456")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .status(ClientStatus.ACTIVE)
                .active(true)
                .addresses(new ArrayList<>())
                .build();
    }

    private ClientRequest createTestRequest() {
        ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("+212600000000");
        request.setCin("AB123456");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return request;
    }

    @Nested
    @DisplayName("Create Client Tests")
    class CreateClientTests {

        @Test
        @DisplayName("Should create client successfully")
        void shouldCreateClientSuccessfully() {
            ClientRequest request = createTestRequest();
            Client client = createTestClient();
            ClientResponse response = new ClientResponse();
            response.setId(CLIENT_ID);

            when(clientRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(clientRepository.existsByCin(request.getCin())).thenReturn(false);
            when(mapper.toDomain(request)).thenReturn(client);
            when(clientRepository.findAll()).thenReturn(new ArrayList<>());
            when(clientRepository.save(any(Client.class))).thenReturn(client);
            when(mapper.toResponse(client)).thenReturn(response);

            ClientResponse result = clientService.createClient(request);

            assertNotNull(result);
            assertEquals(CLIENT_ID, result.getId());
            verify(clientRepository).save(any(Client.class));
            verify(publisher, times(1)).publishEvent(any(Object.class));
            verify(historyService).recordHistory(any(), eq("CLIENT_CREATED"), eq("system"));
        }

        @Test
        @DisplayName("Should throw when email already exists")
        void shouldThrowWhenEmailExists() {
            ClientRequest request = createTestRequest();

            when(clientRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThrows(RuntimeException.class, () -> clientService.createClient(request));
            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when CIN already exists")
        void shouldThrowWhenCinExists() {
            ClientRequest request = createTestRequest();

            when(clientRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(clientRepository.existsByCin(request.getCin())).thenReturn(true);

            assertThrows(RuntimeException.class, () -> clientService.createClient(request));
            verify(clientRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Client Tests")
    class GetClientTests {

        @Test
        @DisplayName("Should return client by ID")
        void shouldReturnClientById() {
            Client client = createTestClient();
            ClientResponse response = new ClientResponse();
            response.setId(CLIENT_ID);

            when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
            when(addressRepository.findByClientId(CLIENT_ID)).thenReturn(new ArrayList<>());
            when(mapper.toResponse(client)).thenReturn(response);

            ClientResponse result = clientService.getClientById(CLIENT_ID);

            assertNotNull(result);
            assertEquals(CLIENT_ID, result.getId());
        }

        @Test
        @DisplayName("Should throw when client not found")
        void shouldThrowWhenClientNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(clientRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> clientService.getClientById(unknownId));
        }

        @Test
        @DisplayName("Should return client by email")
        void shouldReturnClientByEmail() {
            Client client = createTestClient();
            ClientResponse response = new ClientResponse();
            response.setEmail("john.doe@example.com");

            when(clientRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(client));
            when(addressRepository.findByClientId(CLIENT_ID)).thenReturn(new ArrayList<>());
            when(mapper.toResponse(client)).thenReturn(response);

            ClientResponse result = clientService.getClientByEmail("john.doe@example.com");

            assertNotNull(result);
            assertEquals("john.doe@example.com", result.getEmail());
        }

        @Test
        @DisplayName("Should return client by CIN")
        void shouldReturnClientByCin() {
            Client client = createTestClient();
            ClientResponse response = new ClientResponse();
            response.setCin("AB123456");

            when(clientRepository.findByCin("AB123456")).thenReturn(Optional.of(client));
            when(addressRepository.findByClientId(CLIENT_ID)).thenReturn(new ArrayList<>());
            when(mapper.toResponse(client)).thenReturn(response);

            ClientResponse result = clientService.getClientByCin("AB123456");

            assertNotNull(result);
            assertEquals("AB123456", result.getCin());
        }

        @Test
        @DisplayName("Should return all clients")
        void shouldReturnAllClients() {
            List<Client> clients = List.of(createTestClient());
            ClientResponse response = new ClientResponse();

            when(clientRepository.findAll()).thenReturn(clients);
            when(mapper.toResponse(any(Client.class))).thenReturn(response);

            List<ClientResponse> result = clientService.getAllClients();

            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Update Client Tests")
    class UpdateClientTests {

        @Test
        @DisplayName("Should update client successfully")
        void shouldUpdateClientSuccessfully() {
            Client existingClient = createTestClient();
            ClientRequest request = createTestRequest();
            request.setFirstName("Jane");
            Client updatedClient = createTestClient();
            ClientResponse response = new ClientResponse();

            when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(existingClient));
            when(clientRepository.save(any(Client.class))).thenReturn(existingClient);
            when(mapper.toDomain(request)).thenReturn(updatedClient);
            when(mapper.toResponse(existingClient)).thenReturn(response);

            ClientResponse result = clientService.updateClient(CLIENT_ID, request);

            assertNotNull(result);
            verify(clientRepository).save(any(Client.class));
            verify(publisher, times(1)).publishEvent(any(Object.class));
            verify(historyService).recordHistory(CLIENT_ID, "CLIENT_UPDATED", "system");
        }

        @Test
        @DisplayName("Should throw when updating non-existent client")
        void shouldThrowWhenUpdatingNonExistentClient() {
            UUID unknownId = UUID.randomUUID();
            ClientRequest request = createTestRequest();

            when(clientRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> clientService.updateClient(unknownId, request));
            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when new email already exists")
        void shouldThrowWhenNewEmailExists() {
            Client existingClient = createTestClient();
            ClientRequest request = createTestRequest();
            request.setEmail("different@example.com");

            when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(existingClient));
            when(clientRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThrows(RuntimeException.class, () -> clientService.updateClient(CLIENT_ID, request));
            verify(clientRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Client Tests")
    class DeleteClientTests {

        @Test
        @DisplayName("Should delete client successfully")
        void shouldDeleteClientSuccessfully() {
            Client client = createTestClient();

            when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));

            assertDoesNotThrow(() -> clientService.deleteClient(CLIENT_ID));
            verify(clientRepository).deleteById(CLIENT_ID);
            verify(publisher, times(1)).publishEvent(any(Object.class));
            verify(historyService).recordHistory(CLIENT_ID, "CLIENT_DELETED", "system");
        }

        @Test
        @DisplayName("Should throw when deleting non-existent client")
        void shouldThrowWhenDeletingNonExistentClient() {
            UUID unknownId = UUID.randomUUID();

            when(clientRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> clientService.deleteClient(unknownId));
            verify(clientRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Activate/Deactivate Tests")
    class ActivateDeactivateTests {

        @Test
        @DisplayName("Should activate client")
        void shouldActivateClient() {
            Client client = createTestClient();
            client.setActive(false);
            client.setStatus(ClientStatus.INACTIVE);

            when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
            when(clientRepository.save(any(Client.class))).thenReturn(client);

            assertDoesNotThrow(() -> clientService.activateClient(CLIENT_ID));
            verify(clientRepository).save(client);
            verify(historyService).recordHistory(CLIENT_ID, "CLIENT_ACTIVATED", "system");
        }

        @Test
        @DisplayName("Should deactivate client")
        void shouldDeactivateClient() {
            Client client = createTestClient();

            when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
            when(clientRepository.save(any(Client.class))).thenReturn(client);

            assertDoesNotThrow(() -> clientService.deactivateClient(CLIENT_ID));
            verify(clientRepository).save(client);
            verify(historyService).recordHistory(CLIENT_ID, "CLIENT_DEACTIVATED", "system");
        }

        @Test
        @DisplayName("Should throw when activating non-existent client")
        void shouldThrowWhenActivatingNonExistentClient() {
            UUID unknownId = UUID.randomUUID();

            when(clientRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> clientService.activateClient(unknownId));
        }
    }
}
