package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.*;
import com.pfe.iam.application.mapper.UserMapper;
import com.pfe.iam.application.service.AuditService;
import com.pfe.iam.application.service.AuthService;
import com.pfe.iam.application.service.SessionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.pfe.iam.domain.exception.EmailAlreadyExistsException;
import com.pfe.iam.domain.model.PasswordResetToken;
import com.pfe.iam.domain.model.User;
import com.pfe.iam.domain.repository.PasswordResetTokenRepository;
import com.pfe.iam.domain.repository.UserRepository;
import com.pfe.commons.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final UserMapper userMapper;
        private final AuditService auditService;
        private final SessionService sessionService;
        private final PasswordResetTokenRepository passwordResetTokenRepository;

        @Override
        @Transactional
        public UserDto register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new EmailAlreadyExistsException("Email already taken: " + request.getEmail());
                }

                User user = userMapper.toDomain(request);
                user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                user.setActive(true);

                User savedUser = userRepository.save(user);
                auditService.log(savedUser.getId().toString(), "USER_REGISTERED");
                log.info("User registered: {}", savedUser.getEmail());
                return userMapper.toDto(savedUser);
        }

        @Override
        @Cacheable(value = "users", key = "#request.email")
        public TokenResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

                List<String> roles = user.getRole() != null ? List.of(user.getRole().getName().name()) : List.of();

                List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPasswordHash())
                                .authorities(authorities)
                                .build();

                String token = jwtService.generateToken(userDetails, roles);
                sessionService.createSession(user.getId().toString(), token, 86400000L);
                auditService.log(user.getId().toString(), "USER_LOGIN");
                log.info("User logged in: {}", user.getEmail());
                return new TokenResponse(token, userMapper.toDto(user));
        }

        // ===================== LOGOUT =====================

        @Override
        @Transactional
        @CacheEvict(value = "users", key = "#username")
        public void logout(String token) {
                // Remove "Bearer " prefix if present
                if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                }

                String username = jwtService.extractUsername(token);
                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Invalidate all sessions for this user
                sessionService.invalidateAllUserSessions(user.getId().toString());
                auditService.log(user.getId().toString(), "USER_LOGOUT");
                log.info("User logged out: {}", user.getEmail());
        }

        // ===================== FORGOT PASSWORD =====================

        @Override
        @Transactional
        public String forgotPassword(ForgotPasswordRequest request) {
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No account found with email: " + request.getEmail()));

                // Delete any existing reset tokens for this user
                passwordResetTokenRepository.deleteByUserId(user.getId());

                // Generate a new reset token (valid for 30 minutes)
                String resetToken = UUID.randomUUID().toString();
                PasswordResetToken tokenEntity = PasswordResetToken.builder()
                                .userId(user.getId())
                                .token(resetToken)
                                .expiresAt(LocalDateTime.now().plusMinutes(30))
                                .used(false)
                                .createdAt(LocalDateTime.now())
                                .build();

                passwordResetTokenRepository.save(tokenEntity);
                auditService.log(user.getId().toString(), "PASSWORD_RESET_REQUESTED");
                log.info("Password reset token generated for user: {}", user.getEmail());

                // In production, this token would be sent via email (NotificationService)
                // For now, we return it in the response
                return resetToken;
        }

        // ===================== RESET PASSWORD =====================

        @Override
        @Transactional
        @CacheEvict(value = "users", key = "#user.email")
        public void resetPassword(ResetPasswordRequest request) {
                PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

                if (!resetToken.isValid()) {
                        throw new IllegalArgumentException("Reset token is expired or already used");
                }

                User user = userRepository.findById(resetToken.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);

                // Mark token as used
                resetToken.setUsed(true);
                passwordResetTokenRepository.save(resetToken);

                // Invalidate all sessions (force re-login)
                sessionService.invalidateAllUserSessions(user.getId().toString());

                auditService.log(user.getId().toString(), "PASSWORD_RESET_COMPLETED");
                log.info("Password reset completed for user: {}", user.getEmail());
        }

        // ===================== CHANGE PASSWORD =====================

        @Override
        @Transactional
        @CacheEvict(value = "users", key = "#userEmail")
        public void changePassword(String userEmail, ChangePasswordRequest request) {
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Verify old password
                if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
                        throw new IllegalArgumentException("Old password is incorrect");
                }

                user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);

                auditService.log(user.getId().toString(), "PASSWORD_CHANGED");
                log.info("Password changed for user: {}", user.getEmail());
        }
}
