package com.pfe.iam.infrastructure.config;

import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.User;
import com.pfe.iam.infrastructure.persistence.repository.UserRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepositoryAdapter userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Checking if database seeder should run...");

        if (!userRepository.existsByEmail("admin@assureflow.com")) {
            log.info("Seeding Admin user...");
            User admin = User.builder()
                    .id(UUID.randomUUID().toString())
                    .email("admin@assureflow.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Super")
                    .lastName("Admin")
                    .active(true)
                    .roles(Set.of(Role.ADMIN))
                    .build();
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("agent@assureflow.com")) {
            log.info("Seeding Agent user...");
            User agent = User.builder()
                    .id(UUID.randomUUID().toString())
                    .email("agent@assureflow.com")
                    .password(passwordEncoder.encode("agent123"))
                    .firstName("Test")
                    .lastName("Agent")
                    .active(true)
                    .roles(Set.of(Role.AGENT))
                    .build();
            userRepository.save(agent);
        }

        if (!userRepository.existsByEmail("client@assureflow.com")) {
            log.info("Seeding Client user...");
            User client = User.builder()
                    .id(UUID.randomUUID().toString())
                    .email("client@assureflow.com")
                    .password(passwordEncoder.encode("client123"))
                    .firstName("Test")
                    .lastName("Client")
                    .active(true)
                    .roles(Set.of(Role.CLIENT))
                    .build();
            userRepository.save(client);
        }

        log.info("Database seeding completed.");
    }
}
