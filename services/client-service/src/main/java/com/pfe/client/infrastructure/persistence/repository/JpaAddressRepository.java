package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaAddressRepository extends JpaRepository<AddressEntity, String> {
    List<AddressEntity> findByClientId(String clientId);

    void deleteByClientId(String clientId);
}
