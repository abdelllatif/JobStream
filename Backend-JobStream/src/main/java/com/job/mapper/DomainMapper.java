package com.job.mapper;

import com.job.dto.request.DomainCreateRequestDTO;
import com.job.dto.response.DomainResponseDTO;
import com.job.entity.Domain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DomainMapper {

    DomainResponseDTO toResponseDTO(Domain domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    Domain toEntity(DomainCreateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    void updateEntityFromDTO(DomainCreateRequestDTO dto, @MappingTarget Domain domain);
}
