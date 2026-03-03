package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaAddressRepository extends JpaRepository<AddressEntity, UUID> {
    List<AddressEntity> findByClientId(UUID clientId);

    void deleteByClientId(UUID clientId);
}
