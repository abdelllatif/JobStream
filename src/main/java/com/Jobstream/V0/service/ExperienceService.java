package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.ExperienceRequest;
import com.Jobstream.V0.dto.response.ExperienceResponse;

import java.util.List;
import java.util.UUID;

public interface ExperienceService {

    List<ExperienceResponse> getByUserId(UUID userId);

    ExperienceResponse create(UUID userId, ExperienceRequest request);

    ExperienceResponse update(UUID experienceId, UUID userId, ExperienceRequest request);

    void delete(UUID experienceId, UUID userId);
}
