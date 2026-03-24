package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.LoginRequest;
import com.Jobstream.V0.dto.request.RefreshTokenRequest;
import com.Jobstream.V0.dto.request.RegisterRequest;
import com.Jobstream.V0.dto.response.AuthResponse;
import com.Jobstream.V0.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
