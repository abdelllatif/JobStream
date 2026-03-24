package com.job.mapper;

import com.job.dto.request.CompanyCreateRequestDTO;
import com.job.dto.request.CompanyUpdateRequestDTO;
import com.job.dto.response.CompanyResponseDTO;
import com.job.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    Company toEntity(CompanyCreateRequestDTO dto);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    Company toEntity(CompanyUpdateRequestDTO dto);

    @Mapping(target = "userId", expression = "java(company.getOwner() != null ? company.getOwner().getId() : null)")
    CompanyResponseDTO toResponse(Company company);
}

