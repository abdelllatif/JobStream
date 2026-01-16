package com.job.dto.request;

import com.job.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO used for updating a user.
 * All fields are optional so it can be used for partial updates.
 */
@Data
public class UserUpdateRequestDTO {

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    private String email;

    @Size(min = 8, max = 100)
    private String password;

    private Role role;
}



