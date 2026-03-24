package com.job.dto.response;

import com.job.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationPartnerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Role role;
    private String profilePicture;
}

