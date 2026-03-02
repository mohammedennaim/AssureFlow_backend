package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.UpdateUserRequest;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.application.service.AuditService;
import com.pfe.iam.application.service.UserService;
import com.pfe.iam.domain.exception.RoleNotFoundException;
import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.User;
import com.pfe.iam.domain.repository.RoleRepository;
import com.pfe.iam.domain.repository.UserRepository;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditService auditService;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getUsername() != null)
            user.setUsername(request.getUsername());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getActive() != null)
            user.setActive(request.getActive());

        User saved = userRepository.save(user);
        auditService.log(id, "USER_UPDATED");
        log.info("User updated: {}", id);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
        auditService.log(id, "USER_DELETED");
        log.info("User deleted: {}", id);
    }

    @Override
    @Transactional
    public UserDto assignRole(String userId, String roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        user.setRole(role);
        User saved = userRepository.save(user);
        auditService.log(userId, "ROLE_ASSIGNED: " + role.getName().name());
        log.info("Role {} assigned to user {}", role.getName(), userId);
        return toDto(saved);
    }

    @Override
    @Transactional
    public UserDto removeRole(String userId, String roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        if (user.getRole() != null && user.getRole().getId().equals(roleId)) {
            user.setRole(null);
        }
        User saved = userRepository.save(user);
        auditService.log(userId, "ROLE_REMOVED: " + role.getName().name());
        log.info("Role {} removed from user {}", role.getName(), userId);
        return toDto(saved);
    }

    private UserDto toDto(User user) {
        String roleStr = user.getRole() != null ? user.getRole().getName().name() : null;

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .role(roleStr)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
