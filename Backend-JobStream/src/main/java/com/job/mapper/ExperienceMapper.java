package com.job.mapper;

import com.job.dto.request.ExperienceCreateRequestDTO;
import com.job.dto.request.ExperienceUpdateRequestDTO;
import com.job.dto.response.ExperienceResponseDTO;
import com.job.entity.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateProfile", ignore = true)
    Experience toEntity(ExperienceCreateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateProfile", ignore = true)
    Experience toEntity(ExperienceUpdateRequestDTO dto);

    @Mapping(target = "candidateProfileId", expression = "java(experience.getCandidateProfile() != null ? experience.getCandidateProfile().getId() : null)")
    ExperienceResponseDTO toResponse(Experience experience);
}

