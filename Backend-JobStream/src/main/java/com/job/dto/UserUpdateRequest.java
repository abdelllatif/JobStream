package com.job.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserUpdateRequest {
    
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String phone;
    
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;
    
    private String location;
    
    private String website;
    
    private String linkedinProfile;
    
    private String profilePicture;
}
