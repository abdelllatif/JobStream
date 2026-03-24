package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.SkillResponse;
import com.Jobstream.V0.entity.Skill;

public class SkillMapper {

    public static SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .userId(skill.getUser().getId())
                .name(skill.getName())
                .createdAt(skill.getCreatedAt())
                .build();
    }

    private SkillMapper() {}
}
