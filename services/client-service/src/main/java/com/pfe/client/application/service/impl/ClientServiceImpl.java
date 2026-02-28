package com.pfe.client.application.service.impl;

import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;
import com.pfe.client.application.mapper.ClientMapper;
import com.pfe.client.application.service.ClientService;
import com.pfe.client.domain.exception.ClientNotFoundException;
import com.pfe.client.domain.exception.EmailAlreadyExistsException;
import com.pfe.client.domain.model.Client;
import com.pfe.client.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper mapper;

    @Override
    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        log.info("Creating new client with email: {}", request.getEmail());

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        if (clientRepository.existsByCin(request.getCin())) {
            throw new IllegalArgumentException("CIN " + request.getCin() + " is already registered.");
        }

        Client clientToSave = mapper.toDomain(request);
        Client savedClient = clientRepository.save(clientToSave);

        log.info("Client created successfully with ID: {}", savedClient.getId());
        return mapper.toResponse(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientById(String id) {
        log.debug("Fetching client by ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return mapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientByEmail(String email) {
        log.debug("Fetching client by email: {}", email);
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Client with email " + email + " not found"));
        return mapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientByCin(String cin) {
        log.debug("Fetching client by CIN: {}", cin);
        Client client = clientRepository.findByCin(cin)
                .orElseThrow(() -> new IllegalArgumentException("Client with CIN " + cin + " not found"));
        return mapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        log.debug("Fetching all clients");
        return clientRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClientResponse updateClient(String id, ClientRequest request) {
        log.info("Updating client with ID: {}", id);

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        // Check email conflict if changing email
        if (!existingClient.getEmail().equals(request.getEmail())
                && clientRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Check CIN conflict if changing CIN
        if (!existingClient.getCin().equals(request.getCin())
                && clientRepository.existsByCin(request.getCin())) {
            throw new IllegalArgumentException("CIN " + request.getCin() + " is already registered.");
        }

        Client updatedClientData = mapper.toDomain(request);
        existingClient.update(updatedClientData);

        Client savedClient = clientRepository.save(existingClient);

        log.info("Client updated successfully with ID: {}", savedClient.getId());
        return mapper.toResponse(savedClient);
    }

    @Override
    @Transactional
    public void deleteClient(String id) {
        log.info("Deleting client with ID: {}", id);

        if (clientRepository.findById(id).isEmpty()) {
            throw new ClientNotFoundException(id);
        }

        clientRepository.deleteById(id);
        log.info("Client deleted successfully with ID: {}", id);
    }
}
