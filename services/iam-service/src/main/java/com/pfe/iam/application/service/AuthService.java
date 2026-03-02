package com.pfe.iam.application.service;

import com.pfe.iam.application.dto.*;

public interface AuthService {
    UserDto register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    void logout(String token);

    String forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(String userEmail, ChangePasswordRequest request);
}
