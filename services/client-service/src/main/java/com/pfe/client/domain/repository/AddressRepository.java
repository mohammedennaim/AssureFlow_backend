package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository {
    Address save(Address address);

    Optional<Address> findById(UUID id);

    List<Address> findByClientId(UUID clientId);

    List<Address> findByClientIdIn(List<UUID> clientIds);

    void deleteById(UUID id);

    void deleteByClientId(UUID clientId);
}
