package com.pfe.client.application.service;

import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;

import java.util.List;
import com.pfe.client.application.dto.ClientSearchCriteria;

public interface ClientService {
    ClientResponse createClient(ClientRequest request);

    ClientResponse getClientById(String id);

    ClientResponse getClientByEmail(String email);

    ClientResponse getClientByCin(String cin);

    List<ClientResponse> getAllClients();

    List<ClientResponse> getAllClients(int page, int size);

    List<ClientResponse> search(ClientSearchCriteria criteria, int page, int size);

    void activateClient(String id);

    void deactivateClient(String id);

    ClientResponse updateClient(String id, ClientRequest request);

    void deleteClient(String id);
}
