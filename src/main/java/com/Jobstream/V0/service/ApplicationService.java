package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.ApplicationRequest;
import com.Jobstream.V0.dto.request.UpdateApplicationStatusRequest;
import com.Jobstream.V0.dto.response.ApplicationResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    ApplicationResponse apply(UUID userId, ApplicationRequest request);

    PageResponse<ApplicationResponse> getMyApplications(UUID userId, Pageable pageable);

    List<ApplicationResponse> getApplicationsByJob(UUID jobId, UUID recruiterId);

    ApplicationResponse updateStatus(UUID applicationId, UUID recruiterId,
                                      UpdateApplicationStatusRequest request);

    void withdraw(UUID applicationId, UUID userId);
}
