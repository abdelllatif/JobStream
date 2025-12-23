package com.job.mapper;

import com.job.dto.request.JobCreateRequestDTO;
import com.job.dto.request.JobUpdateRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "domain", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "postedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Job toEntity(JobCreateRequestDTO dto);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "domain", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "postedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Job toEntity(JobUpdateRequestDTO dto);

    @Mapping(target = "companyId", expression = "java(job.getCompany() != null ? job.getCompany().getId() : null)")
    @Mapping(target = "domainId", expression = "java(job.getDomain() != null ? job.getDomain().getId() : null)")
    @Mapping(target = "tagIds", expression = "java(job.getTags() != null ? job.getTags().stream().map(tag -> tag.getId()).collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())")
    JobResponseDTO toResponse(Job job);
}

