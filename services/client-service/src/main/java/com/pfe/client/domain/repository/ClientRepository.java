package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);

    Optional<Client> findById(UUID id);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByCin(String cin);

    List<Client> findAll();

    List<Client> findAll(int page, int size);

    void deleteById(UUID id);

    boolean existsByEmail(String email);

    boolean existsByCin(String cin);
}
