package com.job.dto.request;

import com.job.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationCreateRequestDTO {

    @NotNull
    private Long candidateProfileId;

    @NotNull
    private Long jobId;

    // optional: allow providing initial status, default could be set server-side
    private ApplicationStatus status;
}



