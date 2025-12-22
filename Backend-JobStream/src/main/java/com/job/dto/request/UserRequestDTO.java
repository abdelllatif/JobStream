package com.job.dto.request;

import com.job.enums.Role;
import lombok.Data;

/**
 * Legacy DTO kept for backward compatibility.
 * Prefer using {@link UserCreateRequestDTO} for POST and {@link UserUpdateRequestDTO} for PUT/PATCH.
 */
@Data
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
