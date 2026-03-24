package com.job.dto.response;

import com.job.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
    private Long id;
    private String icon;
    private String title;
    private String body;
    /**
     * ISO date-time string.
     */
    private String time;
    private boolean read;
    private NotificationType type;
    private Long jobId;
    private Long candidateProfileId;
}

