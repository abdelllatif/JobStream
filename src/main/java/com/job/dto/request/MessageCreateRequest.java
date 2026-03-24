package com.job.dto.request;

import lombok.Data;

@Data
public class MessageCreateRequest {
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long jobId;
}

