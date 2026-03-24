package com.Jobstream.V0.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ConversationRequest {

    @NotNull(message = "Target user ID is required")
    private UUID targetUserId;
}
