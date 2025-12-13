package com.job.dto.request;

import com.job.enums.Role;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
