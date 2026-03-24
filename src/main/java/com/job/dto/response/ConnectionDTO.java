package com.job.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionDTO {
    private Long userId;
    private String username;
    private String status; // connected, blocked, pending
    private boolean blockedByMe;
    private String firstName;
    private String lastName;
    private String profilePicture;
}
