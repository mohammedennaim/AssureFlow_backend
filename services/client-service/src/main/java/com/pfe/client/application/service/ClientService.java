package com.pfe.client.application.service;

import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;

import java.util.List;
import java.util.UUID;
import com.pfe.client.application.dto.ClientSearchCriteria;

public interface ClientService {
    ClientResponse createClient(ClientRequest request);

    ClientResponse getClientById(UUID id);

    ClientResponse getClientByEmail(String email);

    ClientResponse getClientByCin(String cin);

    List<ClientResponse> getAllClients();

    List<ClientResponse> getAllClients(int page, int size);

    List<ClientResponse> search(ClientSearchCriteria criteria, int page, int size);

    void activateClient(UUID id);

    void deactivateClient(UUID id);

    ClientResponse updateClient(UUID id, ClientRequest request);

    ClientResponse updateMyProfile(String email, ClientRequest request);

    void deleteClient(UUID id);
}
