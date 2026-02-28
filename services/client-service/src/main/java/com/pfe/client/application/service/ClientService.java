package com.pfe.client.application.service;

import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;

import java.util.List;

public interface ClientService {
    ClientResponse createClient(ClientRequest request);

    ClientResponse getClientById(String id);

    ClientResponse getClientByEmail(String email);

    ClientResponse getClientByCin(String cin);

    List<ClientResponse> getAllClients();

    ClientResponse updateClient(String id, ClientRequest request);

    void deleteClient(String id);
}
