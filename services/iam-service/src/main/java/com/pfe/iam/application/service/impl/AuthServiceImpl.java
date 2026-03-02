package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.LoginRequest;
import com.pfe.iam.application.dto.RegisterRequest;
import com.pfe.iam.application.dto.TokenResponse;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.application.mapper.UserMapper;
import com.pfe.iam.application.service.AuditService;
import com.pfe.iam.application.service.AuthService;
import com.pfe.iam.application.service.SessionService;
import com.pfe.iam.domain.exception.EmailAlreadyExistsException;
import com.pfe.iam.domain.model.User;
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

import java.util.List;
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

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already taken: " + request.getEmail());
        }

        User user = userMapper.toDomain(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        auditService.log(savedUser.getId(), "USER_REGISTERED");
        log.info("User registered: {}", savedUser.getEmail());
        return userMapper.toDto(savedUser);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();

        String token = jwtService.generateToken(userDetails, roles);
        sessionService.createSession(user.getId(), token, 86400000L);
        auditService.log(user.getId(), "USER_LOGIN");
        log.info("User logged in: {}", user.getEmail());
        return new TokenResponse(token, userMapper.toDto(user));
    }
}
