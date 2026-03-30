package com.Jobstream.V0.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConnectedUserResponse {

    /** The connection row id (use this for disconnect/remove calls). */
    private UUID connectionId;

    /** The other user's id. */
    private UUID userId;

    private String firstName;
    private String lastName;
    private String email;
    private String headline;
    private String photoUrl;
    private String location;
    private LocalDateTime connectedAt;
}
