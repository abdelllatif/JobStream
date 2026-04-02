package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.security.JwtService;
import com.Jobstream.V0.dto.request.LoginRequest;
import com.Jobstream.V0.dto.request.RefreshTokenRequest;
import com.Jobstream.V0.dto.request.RegisterRequest;
import com.Jobstream.V0.dto.response.AuthResponse;
import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.Profile;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.Provider;
import com.Jobstream.V0.enums.Role;
import com.Jobstream.V0.exception.DuplicateResourceException;
import com.Jobstream.V0.mapper.UserMapper;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.AuthService;
import com.Jobstream.V0.service.UserService;
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
    private final UserService userService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .enabled(true)
                .build();

        Profile profile = Profile.builder()
                .user(user)
                .build();

        user.setProfile(profile);
        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        userService.userIsSuspend(request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String email = jwtService.extractUsername(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(request.getRefreshToken(), user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
