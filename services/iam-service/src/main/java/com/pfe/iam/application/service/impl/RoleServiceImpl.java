package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.CreatePermissionRequest;
import com.pfe.iam.application.dto.CreateRoleRequest;
import com.pfe.iam.application.dto.PermissionDto;
import com.pfe.iam.application.dto.RoleDto;
import com.pfe.iam.application.service.RoleService;
import com.pfe.iam.domain.exception.RoleNotFoundException;
import com.pfe.iam.domain.model.Permission;
import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.UserRole;
import com.pfe.iam.domain.repository.PermissionRepository;
import com.pfe.iam.domain.repository.RoleRepository;
import com.pfe.commons.exceptions.BusinessException;
import com.pfe.commons.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(request.getName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid role name: " + request.getName()
                    + ". Valid values: ADMIN, AGENT, CLIENT, FINANCE");
        }

        if (roleRepository.existsByName(userRole)) {
            throw new BusinessException("Role " + userRole.name() + " already exists");
        }

        Role role = Role.builder()
                .name(userRole)
                .description(request.getDescription())
                .build();

        Role saved = roleRepository.save(role);
        log.info("Role created: {}", saved.getName());
        return toDto(saved);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto getRoleById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
        return toDto(role);
    }

    @Override
    @Transactional
    public void deleteRole(String id) {
        if (roleRepository.findById(id).isEmpty()) {
            throw new RoleNotFoundException(id);
        }
        roleRepository.deleteById(id);
        log.info("Role deleted: {}", id);
    }

    @Override
    @Transactional
    public RoleDto assignPermission(String roleId, String permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));

        role.grantPermission(permission);
        Role saved = roleRepository.save(role);
        log.info("Permission {} assigned to role {}", permissionId, role.getName());
        return toDto(saved);
    }

    @Override
    @Transactional
    public RoleDto removePermission(String roleId, String permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        role.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        Role saved = roleRepository.save(role);
        log.info("Permission {} removed from role {}", permissionId, role.getName());
        return toDto(saved);
    }

    @Override
    @Transactional
    public PermissionDto createPermission(CreatePermissionRequest request) {
        if (permissionRepository.findByResourceAndAction(request.getResource(), request.getAction()).isPresent()) {
            throw new BusinessException("Permission " + request.getResource() + ":" + request.getAction() + " already exists");
        }

        Permission permission = Permission.builder()
                .resource(request.getResource())
                .action(request.getAction())
                .build();

        Permission saved = permissionRepository.save(permission);
        log.info("Permission created: {}:{}", saved.getResource(), saved.getAction());
        return toPermissionDto(saved);
    }

    @Override
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePermission(String id) {
        permissionRepository.deleteById(id);
        log.info("Permission deleted: {}", id);
    }

    private RoleDto toDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName().name())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream()
                        .map(this::toPermissionDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private PermissionDto toPermissionDto(Permission p) {
        return PermissionDto.builder()
                .id(p.getId())
                .resource(p.getResource())
                .action(p.getAction())
                .build();
    }
}
