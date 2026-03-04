package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {
    Permission save(Permission permission);

    Optional<Permission> findById(UUID id);

    Optional<Permission> findByResourceAndAction(String resource, String action);

    List<Permission> findAll();

    void deleteById(UUID id);
}
