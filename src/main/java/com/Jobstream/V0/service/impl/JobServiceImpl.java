package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.JobRequest;
import com.Jobstream.V0.dto.response.JobResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.Company;
import com.Jobstream.V0.entity.Job;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.JobStatus;
import com.Jobstream.V0.enums.JobType;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.JobMapper;
import com.Jobstream.V0.repository.CompanyRepository;
import com.Jobstream.V0.repository.CompanyUserRepository;
import com.Jobstream.V0.repository.JobRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public JobResponse create(UUID userId, JobRequest request) {
        User user = findUser(userId);
        Company company = findCompany(request.getCompanyId());
        assertRecruiterOrHigher(request.getCompanyId(), userId);

        Job job = Job.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .status(request.getStatus())
                .createdBy(user)
                .build();

        return JobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobResponse update(UUID jobId, UUID userId, JobRequest request) {
        Job job = findJob(jobId);
        assertRecruiterOrHigher(job.getCompany().getId(), userId);

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setStatus(request.getStatus());

        return JobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getById(UUID jobId) {
        return JobMapper.toResponse(findJob(jobId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchJobs(String keyword, String location,
                                                 JobType jobType, JobStatus status, Pageable pageable) {
        Page<Job> page = jobRepository.searchJobs(keyword, location, jobType, status, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobsExceptPoster(UUID userId) {
        return jobRepository.findByCreatedByIdNot(userId)
                .stream()
                .map(JobMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getByCompany(UUID companyId) {
        return jobRepository.findByCompanyIdAndStatus(companyId, JobStatus.OPEN)
                .stream().map(JobMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID jobId, UUID userId) {
        Job job = findJob(jobId);
        assertRecruiterOrHigher(job.getCompany().getId(), userId);
        jobRepository.delete(job);
    }

    private PageResponse<JobResponse> buildPageResponse(Page<Job> page) {
        return PageResponse.<JobResponse>builder()
                .content(page.getContent().stream().map(JobMapper::toResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    private void assertRecruiterOrHigher(UUID companyId, UUID userId) {
        if (!companyUserRepository.existsByCompanyIdAndUserId(companyId, userId)) {
            throw new UnauthorizedException("Not a member of this company");
        }
    }

    private Job findJob(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
    }

    private Company findCompany(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
