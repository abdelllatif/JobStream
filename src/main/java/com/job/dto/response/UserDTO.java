package com.job.dto.response;

import com.job.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String profileImagePath;
    private String phone;
    private String bio;
    private String location;
    private String website;
    private String linkedinProfile;
    private boolean emailVerified;
    private boolean premiumUser;
    
    // Linked profile information
    private CandidateProfileResponseDTO candidateProfile;
}
