package com.job.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class MessageRequest {
    
    @NotBlank(message = "Sender ID is required")
    private Long senderId;
    
    @NotBlank(message = "Receiver ID is required")
    private Long receiverId;
    
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    private String content;
    
    private Long jobId;
}
