package com.pfe.client.application.service.impl;

import com.pfe.client.application.dto.ClientRequest;
import com.pfe.client.application.dto.ClientResponse;
import com.pfe.client.application.mapper.ClientMapper;
import com.pfe.client.application.service.ClientHistoryService;
import com.pfe.client.application.service.ClientService;
import com.pfe.client.domain.exception.ClientNotFoundException;
import com.pfe.client.domain.exception.EmailAlreadyExistsException;
import com.pfe.client.domain.exception.CinAlreadyExistsException;
import com.pfe.client.domain.model.Address;
import com.pfe.client.domain.model.Client;
import com.pfe.client.domain.model.ClientStatus;
import com.pfe.client.domain.repository.AddressRepository;
import com.pfe.client.domain.repository.ClientRepository;
import com.pfe.client.application.dto.ClientSearchCriteria;
import com.pfe.client.domain.event.ClientCreatedEvent;
import com.pfe.client.domain.event.ClientUpdatedEvent;
import com.pfe.client.domain.event.ClientDeletedEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ClientMapper mapper;
    private final ApplicationEventPublisher publisher;
    private final ClientHistoryService historyService;

    private String generateClientNumber() {
        long count = clientRepository.findAll().size() + 1;
        return String.format("CLT-%05d", count);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        log.info("Creating new client with email: {}", request.getEmail());

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        if (clientRepository.existsByCin(request.getCin())) {
            throw new CinAlreadyExistsException(request.getCin());
        }

        Client clientToSave = mapper.toDomain(request);
        clientToSave.setClientNumber(generateClientNumber());
        clientToSave.setStatus(ClientStatus.ACTIVE);
        clientToSave.setActive(true);

        Client savedClient = clientRepository.save(clientToSave);

        if (request.getAddresses() != null) {
            List<Address> addresses = request.getAddresses().stream()
                    .map(dto -> {
                        Address addr = mapper.toAddressDomain(dto);
                        addr.setClientId(savedClient.getId());
                        return addressRepository.save(addr);
                    })
                    .collect(Collectors.toList());
            savedClient.setAddresses(addresses);
        }

        publisher.publishEvent(ClientCreatedEvent.builder()
                .clientId(savedClient.getId())
                .client(savedClient)
                .source("client-service")
                .build());

        historyService.recordHistory(savedClient.getId(), "CLIENT_CREATED", "system");

        log.info("Client created successfully with ID: {} and number: {}", savedClient.getId(),
                savedClient.getClientNumber());
        return mapper.toResponse(savedClient);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    @Cacheable(value = "clients", key = "#id")
    public ClientResponse getClientById(UUID id) {
        log.debug("Fetching client from database by ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        client.setAddresses(addressRepository.findByClientId(id));
        return mapper.toResponse(client);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public ClientResponse getClientByEmail(String email) {
        log.debug("Fetching client by email: {}", email);
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Client with email " + email + " not found"));
        client.setAddresses(addressRepository.findByClientId(client.getId()));
        return mapper.toResponse(client);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
    @Transactional(readOnly = true)
    public ClientResponse getClientByCin(String cin) {
        log.debug("Fetching client by CIN: {}", cin);
        Client client = clientRepository.findByCin(cin)
                .orElseThrow(() -> new IllegalArgumentException("Client with CIN " + cin + " not found"));
        client.setAddresses(addressRepository.findByClientId(client.getId()));
        return mapper.toResponse(client);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        log.debug("Fetching all clients");
        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            return List.of();
        }
        List<UUID> clientIds = clients.stream().map(Client::getId).collect(Collectors.toList());
        Map<UUID, List<Address>> addressesByClientId = addressRepository.findByClientIdIn(clientIds)
                .stream()
                .collect(Collectors.groupingBy(Address::getClientId));
        clients.forEach(c -> c.setAddresses(addressesByClientId.getOrDefault(c.getId(), List.of())));
        return clients.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public ClientResponse updateClient(UUID id, ClientRequest request) {
        log.info("Updating client with ID: {}", id);

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (!existingClient.getEmail().equals(request.getEmail())
                && clientRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        if (!existingClient.getCin().equals(request.getCin())
                && clientRepository.existsByCin(request.getCin())) {
            throw new CinAlreadyExistsException(request.getCin());
        }

        Client updatedClientData = mapper.toDomain(request);
        existingClient.update(updatedClientData);

        Client savedClient = clientRepository.save(existingClient);

        if (request.getAddresses() != null) {
            addressRepository.deleteByClientId(id);
            List<Address> addresses = request.getAddresses().stream()
                    .map(dto -> {
                        Address addr = mapper.toAddressDomain(dto);
                        addr.setClientId(id);
                        return addressRepository.save(addr);
                    })
                    .collect(Collectors.toList());
            savedClient.setAddresses(addresses);
        }

        publisher.publishEvent(ClientUpdatedEvent.builder()
                .clientId(savedClient.getId())
                .client(savedClient)
                .source("client-service")
                .build());

        historyService.recordHistory(id, "CLIENT_UPDATED", "system");

        log.info("Client updated successfully with ID: {}", savedClient.getId());
        return mapper.toResponse(savedClient);
    }

    @Override
    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public ClientResponse updateMyProfile(String email, ClientRequest request) {
        log.info("Self-update profile for: {}", email);
        Client existing = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientNotFoundException(null));

        if (request.getFirstName() != null) existing.setFirstName(request.getFirstName());
        if (request.getLastName() != null) existing.setLastName(request.getLastName());
        if (request.getPhone() != null) existing.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) existing.setDateOfBirth(request.getDateOfBirth());
        if (request.getCin() != null && !request.getCin().equals(existing.getCin())) {
            if (clientRepository.existsByCin(request.getCin()))
                throw new CinAlreadyExistsException(request.getCin());
            existing.setCin(request.getCin());
        }
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        Client saved = clientRepository.save(existing);

        if (request.getAddresses() != null && !request.getAddresses().isEmpty()) {
            addressRepository.deleteByClientId(saved.getId());
            request.getAddresses().forEach(dto -> {
                Address addr = mapper.toAddressDomain(dto);
                addr.setClientId(saved.getId());
                addressRepository.save(addr);
            });
        }

        historyService.recordHistory(saved.getId(), "SELF_PROFILE_UPDATED", email);
        return mapper.toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteClient(UUID id) {
        log.info("Deleting client with ID: {}", id);

        if (clientRepository.findById(id).isEmpty()) {
            throw new ClientNotFoundException(id);
        }

        clientRepository.deleteById(id);
        publisher.publishEvent(ClientDeletedEvent.builder()
                .clientId(id)
                .source("client-service")
                .build());

        historyService.recordHistory(id, "CLIENT_DELETED", "system");
        log.info("Client deleted successfully with ID: {}", id);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients(int page, int size) {
        return clientRepository.findAll(page, size).stream()
                .peek(c -> c.setAddresses(addressRepository.findByClientId(c.getId())))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional(readOnly = true)
    public List<ClientResponse> search(ClientSearchCriteria criteria, int page, int size) {
        return clientRepository.findAll(page, size).stream()
                .filter(c -> {
                    if (criteria.getFirstName() != null && (c.getFirstName() == null
                            || !c.getFirstName().toLowerCase().contains(criteria.getFirstName().toLowerCase())))
                        return false;
                    if (criteria.getLastName() != null && (c.getLastName() == null
                            || !c.getLastName().toLowerCase().contains(criteria.getLastName().toLowerCase())))
                        return false;
                    if (criteria.getEmail() != null
                            && (c.getEmail() == null || !c.getEmail().equalsIgnoreCase(criteria.getEmail())))
                        return false;
                    if (criteria.getCin() != null
                            && (c.getCin() == null || !c.getCin().equalsIgnoreCase(criteria.getCin())))
                        return false;
                    if (criteria.getStatus() != null && c.getStatus() != criteria.getStatus())
                        return false;
                    if (criteria.getType() != null && c.getType() != criteria.getType())
                        return false;
                    return true;
                })
                .peek(c -> c.setAddresses(addressRepository.findByClientId(c.getId())))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void activateClient(UUID id) {
        var client = clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
        client.setActive(true);
        client.setStatus(ClientStatus.ACTIVE);
        clientRepository.save(client);
        historyService.recordHistory(id, "CLIENT_ACTIVATED", "system");
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Transactional
    public void deactivateClient(UUID id) {
        var client = clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
        client.setActive(false);
        client.setStatus(ClientStatus.INACTIVE);
        clientRepository.save(client);
        historyService.recordHistory(id, "CLIENT_DEACTIVATED", "system");
    }
}
