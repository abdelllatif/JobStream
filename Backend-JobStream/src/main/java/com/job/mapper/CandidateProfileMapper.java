package com.job.mapper;

import com.job.dto.request.CandidateProfileCreateRequestDTO;
import com.job.dto.request.CandidateProfileUpdateRequestDTO;
import com.job.dto.response.CandidateProfileResponseDTO;
import com.job.entity.CandidateProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    CandidateProfile toEntity(CandidateProfileCreateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    CandidateProfile toEntity(CandidateProfileUpdateRequestDTO dto);

    @Mapping(target = "userId", expression = "java(candidateProfile.getUser() != null ? candidateProfile.getUser().getId() : null)")
    CandidateProfileResponseDTO toResponse(CandidateProfile candidateProfile);
}

