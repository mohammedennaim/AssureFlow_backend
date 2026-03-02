package com.pfe.client.domain.repository;

import com.pfe.client.domain.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository {
    Address save(Address address);

    Optional<Address> findById(String id);

    List<Address> findByClientId(String clientId);

    void deleteById(String id);

    void deleteByClientId(String clientId);
}
