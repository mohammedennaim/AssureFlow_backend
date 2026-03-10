package com.pfe.iam.infrastructure.web.controller;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.iam.application.dto.*;
import com.pfe.iam.application.service.AuditService;
import com.pfe.iam.application.service.SessionService;
import com.pfe.iam.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;
    private final AuditService auditService;

    @PostMapping
    @Operation(summary = "Create a new user (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UserDto>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);
        return ResponseEntity.status(201).body(BaseResponse.success(user, "User created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<BaseResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(BaseResponse.success(users, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<BaseResponse<UserDto>> getUserById(@PathVariable String id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(BaseResponse.success(user, "User retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<BaseResponse<UserDto>> updateUser(
            @PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        UserDto user = userService.updateUser(id, request);
        return ResponseEntity.ok(BaseResponse.success(user, "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "Assign role to user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UserDto>> assignRole(
            @PathVariable String id, @Valid @RequestBody AssignRoleRequest request) {
        UserDto user = userService.assignRole(id, request.getRoleId());
        return ResponseEntity.ok(BaseResponse.success(user, "Role assigned successfully"));
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    @Operation(summary = "Remove role from user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UserDto>> removeRole(
            @PathVariable String id, @PathVariable String roleId) {
        UserDto user = userService.removeRole(id, roleId);
        return ResponseEntity.ok(BaseResponse.success(user, "Role removed successfully"));
    }

    @GetMapping("/{id}/sessions")
    @Operation(summary = "Get user sessions")
    public ResponseEntity<BaseResponse<List<SessionDto>>> getUserSessions(@PathVariable String id) {
        List<SessionDto> sessions = sessionService.getSessionsByUserId(id);
        return ResponseEntity.ok(BaseResponse.success(sessions, "Sessions retrieved successfully"));
    }

    @DeleteMapping("/{id}/sessions")
    @Operation(summary = "Invalidate all user sessions")
    public ResponseEntity<Void> invalidateUserSessions(@PathVariable String id) {
        sessionService.invalidateAllUserSessions(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/audit-logs")
    @Operation(summary = "Get user audit logs")
    public ResponseEntity<BaseResponse<List<AuditLogDto>>> getUserAuditLogs(@PathVariable String id) {
        List<AuditLogDto> logs = auditService.getAuditLogsByUserId(id);
        return ResponseEntity.ok(BaseResponse.success(logs, "Audit logs retrieved successfully"));
    }
}
