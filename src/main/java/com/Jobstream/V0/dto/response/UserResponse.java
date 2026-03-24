package com.Jobstream.V0.dto.response;

import com.Jobstream.V0.enums.Provider;
import com.Jobstream.V0.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Provider provider;
    private boolean enabled;
    private LocalDateTime createdAt;
    private ProfileResponse profile;
}
