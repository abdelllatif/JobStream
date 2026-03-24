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
public class UserNetworkDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private Role role;
    private String bio;
    private String location;

    // Connection status with current user
    // NONE, PENDING_SENT, PENDING_RECEIVED, CONNECTED
    private String connectionStatus;
}
