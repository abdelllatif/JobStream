package com.Jobstream.V0.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConnectedUserResponse {

    private UUID connectionId;

    private UUID userId;

    private String firstName;
    private String lastName;
    private String email;
    private String headline;
    private String photoUrl;
    private String location;
    private LocalDateTime connectedAt;
}
