package com.job.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionListResponseDTO {
    private List<ConnectionDTO> connections;
    private List<Long> blockedUsers; // Users I blocked
    private List<Long> usersWhoBlockedMe;
    private UserResponseDTO currentUser;
}
