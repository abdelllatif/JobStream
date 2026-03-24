package com.Jobstream.V0.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ConnectionRequest {

    @NotNull(message = "Receiver ID is required")
    private UUID receiverId;
}
