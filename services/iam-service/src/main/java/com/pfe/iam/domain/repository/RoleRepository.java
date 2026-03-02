package com.pfe.iam.domain.repository;

import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);

    Optional<Role> findById(String id);

    Optional<Role> findByName(UserRole name);

    List<Role> findAll();

    void deleteById(String id);

    boolean existsByName(UserRole name);
}
