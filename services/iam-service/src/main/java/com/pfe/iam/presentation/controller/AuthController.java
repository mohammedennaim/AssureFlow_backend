package com.pfe.iam.presentation.controller;

import com.pfe.commons.dto.BaseResponse;
import com.pfe.iam.application.dto.LoginRequest;
import com.pfe.iam.application.dto.RegisterRequest;
import com.pfe.iam.application.dto.TokenResponse;
import com.pfe.iam.application.dto.UserDto;
import com.pfe.iam.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<UserDto>> register(@RequestBody RegisterRequest request) {
        UserDto registeredUser = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(registeredUser, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(BaseResponse.success(tokenResponse, "Login successful"));
    }
}
