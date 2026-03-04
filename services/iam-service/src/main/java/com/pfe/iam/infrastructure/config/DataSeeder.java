package com.pfe.iam.infrastructure.config;

import com.pfe.iam.domain.model.Permission;
import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.User;
import com.pfe.iam.domain.model.UserRole;
import com.pfe.iam.domain.repository.PermissionRepository;
import com.pfe.iam.domain.repository.RoleRepository;
import com.pfe.iam.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking if database seeder should run...");
        seedPermissions();
        seedRoles();
        seedUsers();
        log.info("Database seeding completed.");
    }

    private void seedPermissions() {
        if (permissionRepository.findAll().isEmpty()) {
            log.info("Seeding permissions...");
            String[][] perms = {
                    { "users", "read" }, { "users", "write" }, { "users", "delete" },
                    { "policies", "read" }, { "policies", "write" }, { "policies", "approve" },
                    { "claims", "read" }, { "claims", "write" }, { "claims", "approve" },
                    { "billing", "read" }, { "billing", "write" }
            };
            for (String[] p : perms) {
                permissionRepository.save(Permission.builder().resource(p[0]).action(p[1]).build());
            }
            log.info("Seeded {} permissions", perms.length);
        }
    }

    private void seedRoles() {
        for (UserRole ur : UserRole.values()) {
            if (!roleRepository.existsByName(ur)) {
                log.info("Seeding role: {}", ur.name());
                Role role = Role.builder()
                        .name(ur)
                        .description(ur.name() + " role")
                        .build();
                roleRepository.save(role);
            }
        }

        // Assign all permissions to ADMIN role
        roleRepository.findByName(UserRole.ADMIN).ifPresent(adminRole -> {
            if (adminRole.getPermissions().isEmpty()) {
                List<Permission> allPerms = permissionRepository.findAll();
                allPerms.forEach(adminRole::grantPermission);
                roleRepository.save(adminRole);
                log.info("Assigned {} permissions to ADMIN role", allPerms.size());
            }
        });
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@assureflow.com")) {
            log.info("Seeding Admin user...");
            Role adminRole = roleRepository.findByName(UserRole.ADMIN).orElseThrow();
            User admin = User.builder()
                    .id(UUID.randomUUID())
                    .username("admin")
                    .email("admin@assureflow.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .active(true)
                    .role(adminRole)
                    .build();
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("agent@assureflow.com")) {
            log.info("Seeding Agent user...");
            Role agentRole = roleRepository.findByName(UserRole.AGENT).orElseThrow();
            User agent = User.builder()
                    .id(UUID.randomUUID())
                    .username("agent")
                    .email("agent@assureflow.com")
                    .passwordHash(passwordEncoder.encode("agent123"))
                    .active(true)
                    .role(agentRole)
                    .build();
            userRepository.save(agent);
        }

        if (!userRepository.existsByEmail("client@assureflow.com")) {
            log.info("Seeding Client user...");
            Role clientRole = roleRepository.findByName(UserRole.CLIENT).orElseThrow();
            User client = User.builder()
                    .id(UUID.randomUUID())
                    .username("client")
                    .email("client@assureflow.com")
                    .passwordHash(passwordEncoder.encode("client123"))
                    .active(true)
                    .role(clientRole)
                    .build();
            userRepository.save(client);
        }

        log.info("User seeding completed.");
    }
}
