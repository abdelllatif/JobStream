package com.job.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private boolean premiumUser;
}
