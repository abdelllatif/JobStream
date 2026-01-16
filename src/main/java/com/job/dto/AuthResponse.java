package com.job.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private boolean premiumUser;
}
