package com.pfe.iam.infrastructure.persistence.repository;

import com.pfe.iam.domain.model.Permission;
import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.User;
import com.pfe.iam.domain.repository.UserRepository;
import com.pfe.iam.infrastructure.persistence.entity.RoleEntity;
import com.pfe.iam.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final JpaRoleRepository jpaRoleRepository;

    @Override
    public Optional<User> findById(String id) {
        return jpaUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaUserRepository.deleteById(id);
    }

    private User toDomain(UserEntity entity) {
        Role role = entity.getRole() != null ? roleToDomain(entity.getRole()) : null;

        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .active(entity.isActive())
                .role(role)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private Role roleToDomain(RoleEntity re) {
        Set<Permission> permissions = re.getPermissions() != null
                ? re.getPermissions().stream()
                        .map(pe -> Permission.builder()
                                .id(pe.getId())
                                .resource(pe.getResource())
                                .action(pe.getAction())
                                .build())
                        .collect(Collectors.toSet())
                : new HashSet<>();

        return Role.builder()
                .id(re.getId())
                .name(re.getName())
                .description(re.getDescription())
                .permissions(permissions)
                .build();
    }

    private UserEntity toEntity(User domain) {
        RoleEntity roleEntity = null;
        if (domain.getRole() != null) {
            Role role = domain.getRole();
            if (role.getId() != null) {
                roleEntity = jpaRoleRepository.findById(role.getId()).orElse(null);
            } else if (role.getName() != null) {
                roleEntity = jpaRoleRepository.findByName(role.getName()).orElse(null);
            }
        }

        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .active(domain.isActive())
                .role(roleEntity)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
