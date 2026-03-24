package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.EducationResponse;
import com.Jobstream.V0.entity.Education;

public class EducationMapper {

    public static EducationResponse toResponse(Education education) {
        return EducationResponse.builder()
                .id(education.getId())
                .userId(education.getUser().getId())
                .school(education.getSchool())
                .degree(education.getDegree())
                .fieldOfStudy(education.getFieldOfStudy())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .description(education.getDescription())
                .createdAt(education.getCreatedAt())
                .updatedAt(education.getUpdatedAt())
                .build();
    }

    private EducationMapper() {}
}
