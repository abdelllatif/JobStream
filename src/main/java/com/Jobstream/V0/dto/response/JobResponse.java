package com.Jobstream.V0.dto.response;

import com.Jobstream.V0.enums.JobStatus;
import com.Jobstream.V0.enums.JobType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class JobResponse {

    private UUID id;
    private UUID companyId;
    private String companyName;
    private String companyLogoUrl;
    private String title;
    private String description;
    private String location;
    private JobType jobType;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private JobStatus status;
    private UUID createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long applicationCount;
}
