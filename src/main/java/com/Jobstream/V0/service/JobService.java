package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.JobRequest;
import com.Jobstream.V0.dto.response.JobResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.enums.JobStatus;
import com.Jobstream.V0.enums.JobType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface JobService {

    JobResponse create(UUID userId, JobRequest request);

    JobResponse update(UUID jobId, UUID userId, JobRequest request);

    JobResponse getById(UUID jobId);

    PageResponse<JobResponse> searchJobs(String keyword, String location, JobType jobType,
                                          JobStatus status, Pageable pageable);

    List<JobResponse> getByCompany(UUID companyId);

    void delete(UUID jobId, UUID userId);
}
