package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, String> {
    Optional<PermissionEntity> findByResourceAndAction(String resource, String action);
}
