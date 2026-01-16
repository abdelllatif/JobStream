package com.job.mapper;

import com.job.dto.request.CompanyUserCreateRequestDTO;
import com.job.dto.request.CompanyUserUpdateRequestDTO;
import com.job.dto.response.CompanyUserResponseDTO;
import com.job.entity.CompanyUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyUserMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    CompanyUser toEntity(CompanyUserCreateRequestDTO dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    CompanyUser toEntity(CompanyUserUpdateRequestDTO dto);

    @Mapping(target = "userId", expression = "java(companyUser.getUser() != null ? companyUser.getUser().getId() : null)")
    @Mapping(target = "userFirstName", expression = "java(companyUser.getUser() != null ? companyUser.getUser().getFirstName() : null)")
    @Mapping(target = "userLastName", expression = "java(companyUser.getUser() != null ? companyUser.getUser().getLastName() : null)")
    @Mapping(target = "userEmail", expression = "java(companyUser.getUser() != null ? companyUser.getUser().getEmail() : null)")
    @Mapping(target = "companyId", expression = "java(companyUser.getCompany() != null ? companyUser.getCompany().getId() : null)")
    @Mapping(target = "companyName", expression = "java(companyUser.getCompany() != null ? companyUser.getCompany().getName() : null)")
    CompanyUserResponseDTO toResponse(CompanyUser companyUser);
}

