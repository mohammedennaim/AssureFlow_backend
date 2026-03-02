package com.pfe.iam.application.service;

import com.pfe.iam.application.dto.CreatePermissionRequest;
import com.pfe.iam.application.dto.CreateRoleRequest;
import com.pfe.iam.application.dto.PermissionDto;
import com.pfe.iam.application.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto createRole(CreateRoleRequest request);

    List<RoleDto> getAllRoles();

    RoleDto getRoleById(String id);

    void deleteRole(String id);

    RoleDto assignPermission(String roleId, String permissionId);

    RoleDto removePermission(String roleId, String permissionId);

    // Permission management
    PermissionDto createPermission(CreatePermissionRequest request);

    List<PermissionDto> getAllPermissions();

    void deletePermission(String id);
}
