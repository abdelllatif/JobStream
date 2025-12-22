package com.job.dto.response;

import com.job.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResponseDTO {
    private Long id;
    private Long candidateProfileId;
    private Long jobId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}



