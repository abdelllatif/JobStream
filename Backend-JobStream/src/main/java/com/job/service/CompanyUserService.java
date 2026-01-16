package com.job.service;

import com.job.dto.request.CompanyUserCreateRequestDTO;
import com.job.dto.request.CompanyUserUpdateRequestDTO;
import com.job.dto.response.CompanyUserResponseDTO;

import java.util.List;

public interface CompanyUserService {
    CompanyUserResponseDTO joinCompany(CompanyUserCreateRequestDTO dto, Long userId);
    List<CompanyUserResponseDTO> getCompanyUsers(Long companyId);
    List<CompanyUserResponseDTO> getUserCompanies(Long userId);
    CompanyUserResponseDTO updateCompanyUser(Long id, CompanyUserUpdateRequestDTO dto);
    void leaveCompany(Long companyId, Long userId);
    CompanyUserResponseDTO getById(Long id);
}

