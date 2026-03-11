package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.CreateUserRequest;
import com.pfe.iam.application.dto.UpdateUserRequest;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.application.service.AuditService;
import com.pfe.iam.application.service.UserService;
import com.pfe.iam.domain.exception.EmailAlreadyExistsException;
import com.pfe.iam.domain.exception.RoleNotFoundException;
import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.User;
import com.pfe.iam.domain.model.UserRole;
import com.pfe.iam.domain.repository.RoleRepository;
import com.pfe.iam.domain.repository.UserRepository;
import com.pfe.commons.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already taken: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                UserRole roleEnum = UserRole.valueOf(request.getRole().toUpperCase());
                Role role = roleRepository.findByName(roleEnum)
                        .orElseThrow(() -> new RoleNotFoundException(request.getRole()));
                user.setRole(role);
            } catch (IllegalArgumentException e) {
                throw new RoleNotFoundException(request.getRole());
            }
        } else {
            roleRepository.findByName(UserRole.CLIENT).ifPresent(user::setRole);
        }

        User saved = userRepository.save(user);
        auditService.log(saved.getId().toString(), "USER_CREATED_BY_ADMIN");
        log.info("User created by admin: {}", saved.getEmail());
        return toDto(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public UserDto getUserById(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toDto(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return toDto(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(UUID.fromString(id))
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
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(String id) {
        if (userRepository.findById(UUID.fromString(id)).isEmpty()) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(UUID.fromString(id));
        auditService.log(id, "USER_DELETED");
        log.info("User deleted: {}", id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto assignRole(String userId, String roleId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Role role = roleRepository.findById(UUID.fromString(roleId))
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        user.setRole(role);
        User saved = userRepository.save(user);
        auditService.log(userId, "ROLE_ASSIGNED: " + role.getName().name());
        log.info("Role {} assigned to user {}", role.getName(), userId);
        return toDto(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto removeRole(String userId, String roleId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Role role = roleRepository.findById(UUID.fromString(roleId))
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        if (user.getRole() != null && user.getRole().getId().toString().equals(roleId)) {
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
                .id(user.getId() != null ? user.getId().toString() : null)
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .role(roleStr)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
