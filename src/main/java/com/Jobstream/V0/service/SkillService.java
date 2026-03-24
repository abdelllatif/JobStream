package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.SkillRequest;
import com.Jobstream.V0.dto.response.SkillResponse;

import java.util.List;
import java.util.UUID;

public interface SkillService {

    List<SkillResponse> getByUserId(UUID userId);

    SkillResponse addSkill(UUID userId, SkillRequest request);

    void deleteSkill(UUID skillId, UUID userId);
}
