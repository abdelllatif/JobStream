package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.EducationRequest;
import com.Jobstream.V0.dto.response.EducationResponse;

import java.util.List;
import java.util.UUID;

public interface EducationService {

    List<EducationResponse> getByUserId(UUID userId);

    EducationResponse create(UUID userId, EducationRequest request);

    EducationResponse update(UUID educationId, UUID userId, EducationRequest request);

    void delete(UUID educationId, UUID userId);
}
