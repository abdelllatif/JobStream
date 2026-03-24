package com.job.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNetworkResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profileImagePath;
    private String role;
    private String location;
    private String bio;
    private String website;
    private String linkedinProfile;
    private String jobTitle;
    private String connectionStatus;
}
