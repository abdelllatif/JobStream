package com.Jobstream.V0.dto.response;

import com.Jobstream.V0.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private NotificationType type;
    private UUID entityId;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
}
