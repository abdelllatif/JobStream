package com.Jobstream.V0.dto.request;

import com.Jobstream.V0.enums.JobStatus;
import com.Jobstream.V0.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class JobRequest {

    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @NotBlank(message = "Job title is required")
    private String title;

    private String description;
    private String location;
    private JobType jobType;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;

    @NotNull(message = "Job status is required")
    private JobStatus status;
}
