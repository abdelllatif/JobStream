package com.job.controller;

import com.job.dto.request.*;
import com.job.dto.response.*;
import com.job.security.JwtService;
import com.job.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserCreateRequestDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(), dto.getPassword()
                )
        );

        String token = jwtService.generateToken(dto.getEmail());
        return new AuthResponseDTO(token);
    }

    @PostMapping("/logout")
    public String logout() {
        return "Logged out successfully";
    }
}
