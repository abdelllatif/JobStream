package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.CompanyResponse;
import com.Jobstream.V0.dto.response.CompanyUserResponse;
import com.Jobstream.V0.entity.Company;
import com.Jobstream.V0.entity.CompanyUser;

public class CompanyMapper {

    public static CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .logoUrl(company.getLogoUrl())
                .website(company.getWebsite())
                .location(company.getLocation())
                .domain(company.getDomain())
                .createdById(company.getCreatedBy().getId())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .employeeCount(company.getEmployees().size())
                .build();
    }

    public static CompanyUserResponse toEmployeeResponse(CompanyUser cu) {
        String headline = cu.getUser().getProfile() != null ? cu.getUser().getProfile().getHeadline() : null;
        String photoUrl = cu.getUser().getProfile() != null ? cu.getUser().getProfile().getPhotoUrl() : null;
        return CompanyUserResponse.builder()
                .id(cu.getId())
                .userId(cu.getUser().getId())
                .userEmail(cu.getUser().getEmail())
                .userHeadline(headline)
                .userPhotoUrl(photoUrl)
                .companyId(cu.getCompany().getId())
                .companyName(cu.getCompany().getName())
                .role(cu.getRole())
                .startDate(cu.getStartDate())
                .endDate(cu.getEndDate())
                .isCurrent(cu.isCurrent())
                .createdAt(cu.getCreatedAt())
                .build();
    }

    private CompanyMapper() {}
}
