package com.Jobstream.V0.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplicationRequest {

    @NotNull(message = "Job ID is required")
    private UUID jobId;

    private String cvUrl;
    private String coverLetter;
}
