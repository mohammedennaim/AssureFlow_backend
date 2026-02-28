package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.LoginRequest;
import com.pfe.iam.application.dto.RegisterRequest;
import com.pfe.iam.application.dto.TokenResponse;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.application.mapper.UserMapper;
import com.pfe.iam.application.service.AuthService;
import com.pfe.iam.domain.exception.EmailAlreadyExistsException;
import com.pfe.iam.domain.model.User;
import com.pfe.iam.domain.repository.UserRepository;
import com.pfe.iam.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

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
        return userMapper.toDto(savedUser);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateToken(user);
        return new TokenResponse(token, userMapper.toDto(user));
    }
}
