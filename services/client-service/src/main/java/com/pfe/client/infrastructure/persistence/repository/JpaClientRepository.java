package com.pfe.client.infrastructure.persistence.repository;

import com.pfe.client.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaClientRepository extends JpaRepository<ClientEntity, UUID> {
    Optional<ClientEntity> findByEmail(String email);

    Optional<ClientEntity> findByCin(String cin);

    boolean existsByEmail(String email);

    boolean existsByCin(String cin);
}
