package com.job.dto.response;

import com.job.enums.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String profileImagePath;
}
