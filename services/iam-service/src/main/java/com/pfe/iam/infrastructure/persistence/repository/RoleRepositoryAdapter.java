package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.domain.model.Permission;
import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.UserRole;
import com.pfe.iam.domain.repository.RoleRepository;
import com.pfe.iam.infrastructure.persistence.entity.PermissionEntity;
import com.pfe.iam.infrastructure.persistence.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final JpaRoleRepository jpaRoleRepository;

    @Override
    public Role save(Role role) {
        RoleEntity entity = toEntity(role);
        RoleEntity saved = jpaRoleRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return jpaRoleRepository.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public Optional<Role> findByName(UserRole name) {
        return jpaRoleRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return jpaRoleRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRoleRepository.deleteById(id.toString());
    }

    @Override
    public boolean existsByName(UserRole name) {
        return jpaRoleRepository.existsByName(name);
    }

    public Role toDomain(RoleEntity entity) {
        return Role.builder()
                .id(UUID.fromString(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .permissions(entity.getPermissions().stream()
                        .map(this::permissionToDomain)
                        .collect(Collectors.toSet()))
                .build();
    }

    public RoleEntity toEntity(Role role) {
        return RoleEntity.builder()
                .id(role.getId() != null ? role.getId().toString() : null)
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream()
                        .map(this::permissionToEntity)
                        .collect(java.util.stream.Collectors.toSet()))
                .build();
    }

    private Permission permissionToDomain(PermissionEntity entity) {
        return Permission.builder()
                .id(UUID.fromString(entity.getId()))
                .resource(entity.getResource())
                .action(entity.getAction())
                .build();
    }

    private PermissionEntity permissionToEntity(Permission permission) {
        return PermissionEntity.builder()
                .id(permission.getId() != null ? permission.getId().toString() : null)
                .resource(permission.getResource())
                .action(permission.getAction())
                .build();
    }
}
