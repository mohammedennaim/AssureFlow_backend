package com.pfe.iam.presentation.controller;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.iam.application.dto.*;
import com.pfe.iam.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication & password management APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<BaseResponse<UserDto>> register(@RequestBody RegisterRequest request) {
        UserDto registeredUser = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(registeredUser, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<BaseResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(BaseResponse.success(tokenResponse, "Login successful"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate session")
    public ResponseEntity<BaseResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok(BaseResponse.success(null, "Logout successful"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset token")
    public ResponseEntity<BaseResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String resetToken = authService.forgotPassword(request);
        return ResponseEntity
                .ok(BaseResponse.success(resetToken, "Password reset token generated. Use it to reset your password."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using a reset token")
    public ResponseEntity<BaseResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(BaseResponse.success(null, "Password reset successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<BaseResponse<Void>> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        // We need the user ID — get it via the email from the token subject
        authService.changePassword(userEmail, request);
        return ResponseEntity.ok(BaseResponse.success(null, "Password changed successfully"));
    }
}
