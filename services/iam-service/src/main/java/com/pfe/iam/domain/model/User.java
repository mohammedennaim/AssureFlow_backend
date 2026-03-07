package com.pfe.iam.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private boolean active;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Hashes a plain-text password using BCrypt and stores it on this user.
     * Never stores the plain-text password.
     *
     * @param plainPassword the raw password provided by the user
     * @throws IllegalArgumentException if password is null or blank
     */
    public void setPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank.");
        }
        if (plainPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        this.passwordHash = PASSWORD_ENCODER.encode(plainPassword);
    }

    /**
     * Verifies a plain-text password against the stored BCrypt hash.
     *
     * @param plainPassword the raw password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword) {
        if (plainPassword == null || this.passwordHash == null) {
            return false;
        }
        return PASSWORD_ENCODER.matches(plainPassword, this.passwordHash);
    }
}
