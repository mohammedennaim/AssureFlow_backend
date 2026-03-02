package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.domain.model.Permission;
import com.pfe.iam.domain.repository.PermissionRepository;
import com.pfe.iam.infrastructure.persistence.entity.PermissionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final JpaPermissionRepository jpaPermissionRepository;

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = toEntity(permission);
        PermissionEntity saved = jpaPermissionRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Permission> findById(String id) {
        return jpaPermissionRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Permission> findByResourceAndAction(String resource, String action) {
        return jpaPermissionRepository.findByResourceAndAction(resource, action).map(this::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return jpaPermissionRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaPermissionRepository.deleteById(id);
    }

    private Permission toDomain(PermissionEntity entity) {
        return Permission.builder()
                .id(entity.getId())
                .resource(entity.getResource())
                .action(entity.getAction())
                .build();
    }

    private PermissionEntity toEntity(Permission permission) {
        return PermissionEntity.builder()
                .id(permission.getId())
                .resource(permission.getResource())
                .action(permission.getAction())
                .build();
    }
}
