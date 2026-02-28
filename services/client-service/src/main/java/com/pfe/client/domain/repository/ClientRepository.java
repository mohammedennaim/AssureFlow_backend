package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);

    Optional<Client> findById(String id);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByCin(String cin);

    List<Client> findAll();

    void deleteById(String id);

    boolean existsByEmail(String email);

    boolean existsByCin(String cin);
}
