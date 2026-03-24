package com.job.mapper;

import com.job.dto.request.EducationCreateRequestDTO;
import com.job.dto.request.EducationUpdateRequestDTO;
import com.job.dto.response.EducationResponseDTO;
import com.job.entity.Education;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EducationMapper {

    @Mapping(target = "candidateProfile", ignore = true)
    Education toEntity(EducationCreateRequestDTO dto);

    @Mapping(target = "candidateProfile", ignore = true)
    Education toEntity(EducationUpdateRequestDTO dto);

    @Mapping(target = "candidateProfileId", expression = "java(education.getCandidateProfile() != null ? education.getCandidateProfile().getId() : null)")
    EducationResponseDTO toResponse(Education education);
}

