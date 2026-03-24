package com.job.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponseDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long jobId;
    /**
     * ISO string used by frontend as createdAt/createdAt date.
     */
    private String createdAt;
    private boolean read;
}

