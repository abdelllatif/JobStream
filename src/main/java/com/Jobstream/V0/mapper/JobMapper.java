package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.JobResponse;
import com.Jobstream.V0.entity.Job;

public class JobMapper {

    public static JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .companyLogoUrl(job.getCompany().getLogoUrl())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .status(job.getStatus())
                .createdById(job.getCreatedBy().getId())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .applicationCount(job.getApplications().size())
                .build();
    }

    private JobMapper() {}
}
