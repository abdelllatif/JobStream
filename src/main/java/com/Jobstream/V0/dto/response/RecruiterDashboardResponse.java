package com.Jobstream.V0.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RecruiterDashboardResponse {

    private UUID companyId;
    private String companyName;
    private long totalJobs;
    private long openJobs;
    private long totalApplications;
    private long pendingApplications;
    private long acceptedApplications;
    private long rejectedApplications;
    private List<JobApplicationStats> jobStats;

    @Data
    @Builder
    public static class JobApplicationStats {
        private UUID jobId;
        private String jobTitle;
        private long totalApplications;
        private long pendingCount;
        private long acceptedCount;
        private long rejectedCount;
    }
}
