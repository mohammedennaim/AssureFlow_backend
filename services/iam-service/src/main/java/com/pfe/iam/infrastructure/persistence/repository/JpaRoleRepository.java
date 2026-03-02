package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.domain.model.UserRole;
import com.pfe.iam.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByName(UserRole name);

    boolean existsByName(UserRole name);
}
