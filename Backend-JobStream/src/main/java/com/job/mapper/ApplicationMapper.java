package com.job.mapper;

import com.job.dto.request.ApplicationCreateRequestDTO;
import com.job.dto.request.ApplicationUpdateRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;
import com.job.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateProfile", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    Application toEntity(ApplicationCreateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidateProfile", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    Application toEntity(ApplicationUpdateRequestDTO dto);

    @Mapping(target = "candidateProfileId", expression = "java(application.getCandidateProfile() != null ? application.getCandidateProfile().getId() : null)")
    @Mapping(target = "jobId", expression = "java(application.getJob() != null ? application.getJob().getId() : null)")
    ApplicationResponseDTO toResponse(Application application);
}

