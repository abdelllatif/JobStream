package com.job.controller;

import com.job.dto.request.LoginRequestDTO;
import com.job.dto.request.UserCreateRequestDTO;
import com.job.dto.response.AuthResponse;
import com.job.dto.response.UserResponseDTO;
import com.job.entity.User;
import com.job.security.JwtService;
import com.job.service.UserService;
import com.job.repository.UserRepository;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthUtil authUtil;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserCreateRequestDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(), dto.getPassword()
                )
        );

        UserResponseDTO userDto = userService.getByEmail(dto.getEmail());
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userDto.getId()));

        String token = jwtService.generateToken(dto.getEmail(), userDto.getId());
        String refreshToken = jwtService.generateRefreshToken(dto.getEmail(), userDto.getId());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setProfilePicture(user.getProfilePicture());
        response.setPremiumUser(user.isPremiumUser());

        return response;
    }

    @GetMapping("/validate-token")
    public AuthResponse validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setProfilePicture(user.getProfilePicture());
        response.setPremiumUser(user.isPremiumUser());

        return response;
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody java.util.Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId());

        AuthResponse response = new AuthResponse();
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setProfilePicture(user.getProfilePicture());
        response.setPremiumUser(user.isPremiumUser());

        return response;
    }

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser() {
        Long userId = authUtil.getCurrentUserId();
        return userService.getById(userId);
    }

    @PostMapping("/logout")
    public String logout() {
        return "Logged out successfully";
    }
}
