package com.job.mapper;

import com.job.dto.request.JobCreateRequestDTO;
import com.job.dto.request.JobUpdateRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.entity.Job;
import com.job.entity.Tag;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, imports = {Tag.class, Collections.class, Collectors.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobMapper {

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "domain", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "postedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applications", ignore = true)
    Job toEntity(JobCreateRequestDTO dto);

    @Mapping(target = "company", ignore = true)
    @Mapping(target = "domain", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "postedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "applications", ignore = true)
    Job toEntity(JobUpdateRequestDTO dto);

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "domainId", source = "domain.id")
    @Mapping(target = "tagIds", expression = "java(job.getTags() != null ? job.getTags().stream().map(Tag::getId).collect(Collectors.toList()) : Collections.emptyList())")
    @Mapping(target = "poster", source = "company.owner")
    @Mapping(target = "applicationCount", ignore = true)
    JobResponseDTO toResponse(Job job);

    @AfterMapping
    default void mapPosterAndCount(Job job, @MappingTarget JobResponseDTO dto) {
        if (job.getApplications() != null) {
            dto.setApplicationCount((long) job.getApplications().size());
        } else {
            dto.setApplicationCount(0L);
        }
    }
}

