package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.response.RecruiterDashboardResponse;
import com.Jobstream.V0.entity.Company;
import com.Jobstream.V0.enums.ApplicationStatus;
import com.Jobstream.V0.enums.JobStatus;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.repository.ApplicationRepository;
import com.Jobstream.V0.repository.CompanyRepository;
import com.Jobstream.V0.repository.CompanyUserRepository;
import com.Jobstream.V0.repository.JobRepository;
import com.Jobstream.V0.service.RecruiterDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterDashboardServiceImpl implements RecruiterDashboardService {

    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional(readOnly = true)
    public RecruiterDashboardResponse getDashboard(UUID companyId, UUID recruiterId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        if (!companyUserRepository.existsByCompanyIdAndUserId(companyId, recruiterId)) {
            throw new UnauthorizedException("Not a member of this company");
        }

        long totalJobs = jobRepository.countByCompanyId(companyId);
        long openJobs = jobRepository.countByCompanyIdAndStatus(companyId, JobStatus.OPEN);

        List<RecruiterDashboardResponse.JobApplicationStats> jobStats = jobRepository.findByCompanyId(companyId)
                .stream()
                .map(job -> {
                    long pending = applicationRepository.countByJobIdAndStatus(job.getId(), ApplicationStatus.PENDING);
                    long accepted = applicationRepository.countByJobIdAndStatus(job.getId(), ApplicationStatus.ACCEPTED);
                    long rejected = applicationRepository.countByJobIdAndStatus(job.getId(), ApplicationStatus.REJECTED);
                    long totalApps = pending + accepted + rejected;

                    return RecruiterDashboardResponse.JobApplicationStats.builder()
                            .jobId(job.getId())
                            .jobTitle(job.getTitle())
                            .totalApplications(totalApps)
                            .pendingCount(pending)
                            .acceptedCount(accepted)
                            .rejectedCount(rejected)
                            .build();
                })
                .collect(Collectors.toList());

        long totalApps = jobStats.stream().mapToLong(RecruiterDashboardResponse.JobApplicationStats::getTotalApplications).sum();
        long pendingApps = jobStats.stream().mapToLong(RecruiterDashboardResponse.JobApplicationStats::getPendingCount).sum();
        long acceptedApps = jobStats.stream().mapToLong(RecruiterDashboardResponse.JobApplicationStats::getAcceptedCount).sum();
        long rejectedApps = jobStats.stream().mapToLong(RecruiterDashboardResponse.JobApplicationStats::getRejectedCount).sum();

        return RecruiterDashboardResponse.builder()
                .companyId(company.getId())
                .companyName(company.getName())
                .totalJobs(totalJobs)
                .openJobs(openJobs)
                .totalApplications(totalApps)
                .pendingApplications(pendingApps)
                .acceptedApplications(acceptedApps)
                .rejectedApplications(rejectedApps)
                .jobStats(jobStats)
                .build();
    }
}
