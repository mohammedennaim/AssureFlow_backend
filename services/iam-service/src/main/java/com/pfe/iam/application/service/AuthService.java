package com.pfe.iam.application.service;

import com.pfe.iam.application.dto.LoginRequest;
import com.pfe.iam.application.dto.RegisterRequest;
import com.pfe.iam.application.dto.TokenResponse;
import com.pfe.iam.application.dto.UserDto;

public interface AuthService {
    UserDto register(RegisterRequest request);

    TokenResponse login(LoginRequest request);
}
