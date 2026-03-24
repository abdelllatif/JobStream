package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.ExperienceResponse;
import com.Jobstream.V0.entity.Experience;

public class ExperienceMapper {

    public static ExperienceResponse toResponse(Experience experience) {
        return ExperienceResponse.builder()
                .id(experience.getId())
                .userId(experience.getUser().getId())
                .companyId(experience.getCompany() != null ? experience.getCompany().getId() : null)
                .companyName(experience.getCompany() != null ? experience.getCompany().getName() : null)
                .title(experience.getTitle())
                .employmentType(experience.getEmploymentType())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .isCurrent(experience.isCurrent())
                .description(experience.getDescription())
                .createdAt(experience.getCreatedAt())
                .updatedAt(experience.getUpdatedAt())
                .build();
    }

    private ExperienceMapper() {}
}
