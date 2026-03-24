package com.Jobstream.V0.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ConversationResponse {

    private UUID id;
    private List<UserResponse> participants;
    private MessageResponse lastMessage;
    private long unreadCount;
    private LocalDateTime createdAt;
}
