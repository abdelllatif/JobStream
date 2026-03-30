package com.Jobstream.V0.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MessageResponse {

    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private String senderEmail;
    private String senderPhotoUrl;
    private String content;
    private UUID jobId;
    private String jobTitle;
    @JsonProperty("isRead")
    private boolean isRead;
    private LocalDateTime createdAt;
}
