package com.pfe.iam.application.service.impl;

import com.pfe.iam.application.dto.LoginRequest;
import com.pfe.iam.application.dto.RegisterRequest;
import com.pfe.iam.application.dto.TokenResponse;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.application.mapper.UserMapper;
import com.pfe.iam.application.service.AuditService;
import com.pfe.iam.application.service.SessionService;
import com.pfe.iam.domain.exception.EmailAlreadyExistsException;
import com.pfe.iam.domain.model.Role;
import com.pfe.iam.domain.model.User;
import com.pfe.iam.domain.model.UserRole;
import com.pfe.iam.domain.repository.PasswordResetTokenRepository;
import com.pfe.iam.domain.repository.RoleRepository;
import com.pfe.iam.domain.repository.UserRepository;
import com.pfe.commons.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuditService auditService;
    @Mock
    private SessionService sessionService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private UserDto userDto;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .username("testuser")
                .active(true)
                .role(Role.builder().id(UUID.randomUUID())
                        .name(UserRole.CLIENT).build())
                .build();

        userDto = new UserDto();
        userDto.setId(userId.toString());
        userDto.setEmail("test@example.com");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setUsername("testuser");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should successfully register a new user")
        void shouldRegisterSuccessfully() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(userMapper.toDomain(registerRequest)).thenReturn(user);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedpassword");
            when(roleRepository.findByName(UserRole.CLIENT)).thenReturn(Optional.of(user.getRole()));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toDto(any(User.class))).thenReturn(userDto);

            UserDto result = authService.register(registerRequest);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(userRepository).save(any(User.class));
            verify(auditService).log(userId.toString(), "USER_REGISTERED");
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequest));

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should successfully login and return a token")
        void shouldLoginSuccessfully() {
            Authentication authentication = mock(Authentication.class);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(jwtService.generateToken(any(), anyList())).thenReturn("dummy.jwt.token");
            when(userMapper.toDto(any(User.class))).thenReturn(userDto);

            TokenResponse response = authService.login(loginRequest);

            assertThat(response).isNotNull();
            assertThat(response.getToken()).isEqualTo("dummy.jwt.token");
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(sessionService).createSession(userId.toString(), "dummy.jwt.token", 86400000L);
            verify(auditService).log(userId.toString(), "USER_LOGIN");
        }

        @Test
        @DisplayName("Should throw exception for unknown user after successful auth attempt")
        void shouldThrowExceptionForUnknownUser() {
            Authentication authentication = mock(Authentication.class);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
        }
    }
}
