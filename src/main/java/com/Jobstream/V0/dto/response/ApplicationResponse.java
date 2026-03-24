package com.Jobstream.V0.dto.response;

import com.Jobstream.V0.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ApplicationResponse {

    private UUID id;
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private UUID userId;
    private String userEmail;
    private String userHeadline;
    private String userPhotoUrl;
    private String cvUrl;
    private ApplicationStatus status;
    private String coverLetter;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
