package com.pfe.iam.infrastructure.web.controller;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.iam.application.dto.*;
import com.pfe.iam.application.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role and permission management APIs")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<BaseResponse<RoleDto>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleDto role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(role, "Role created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<BaseResponse<List<RoleDto>>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(BaseResponse.success(roles, "Roles retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<BaseResponse<RoleDto>> getRoleById(@PathVariable String id) {
        RoleDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(BaseResponse.success(role, "Role retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions/{permissionId}")
    @Operation(summary = "Assign permission to role")
    public ResponseEntity<BaseResponse<RoleDto>> assignPermission(
            @PathVariable String id, @PathVariable String permissionId) {
        RoleDto role = roleService.assignPermission(id, permissionId);
        return ResponseEntity.ok(BaseResponse.success(role, "Permission assigned successfully"));
    }

    @DeleteMapping("/{id}/permissions/{permissionId}")
    @Operation(summary = "Remove permission from role")
    public ResponseEntity<BaseResponse<RoleDto>> removePermission(
            @PathVariable String id, @PathVariable String permissionId) {
        RoleDto role = roleService.removePermission(id, permissionId);
        return ResponseEntity.ok(BaseResponse.success(role, "Permission removed successfully"));
    }

    @PostMapping("/permissions")
    @Operation(summary = "Create a new permission")
    public ResponseEntity<BaseResponse<PermissionDto>> createPermission(
            @Valid @RequestBody CreatePermissionRequest request) {
        PermissionDto permission = roleService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(permission, "Permission created successfully"));
    }

    @GetMapping("/permissions")
    @Operation(summary = "Get all permissions")
    public ResponseEntity<BaseResponse<List<PermissionDto>>> getAllPermissions() {
        List<PermissionDto> permissions = roleService.getAllPermissions();
        return ResponseEntity.ok(BaseResponse.success(permissions, "Permissions retrieved successfully"));
    }

    @DeleteMapping("/permissions/{id}")
    @Operation(summary = "Delete permission")
    public ResponseEntity<Void> deletePermission(@PathVariable String id) {
        roleService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
