package com.job.mapper;

import com.job.dto.request.CompanyCreateRequestDTO;
import com.job.dto.request.CompanyUpdateRequestDTO;
import com.job.dto.response.CompanyResponseDTO;
import com.job.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    Company toEntity(CompanyCreateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    Company toEntity(CompanyUpdateRequestDTO dto);

    @Mapping(target = "userId", expression = "java(company.getUser() != null ? company.getUser().getId() : null)")
    CompanyResponseDTO toResponse(Company company);
}

