package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.AddCompanyEmployeeRequest;
import com.Jobstream.V0.dto.request.CompanyRequest;
import com.Jobstream.V0.dto.response.CompanyResponse;
import com.Jobstream.V0.dto.response.CompanyUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CompanyService {

    CompanyResponse create(UUID userId, CompanyRequest request);

    CompanyResponse update(UUID companyId, UUID userId, CompanyRequest request);

    CompanyResponse getById(UUID companyId);

    Page<CompanyResponse> search(String query, Pageable pageable);

    List<CompanyResponse> getMyCompanies(UUID userId);

    void delete(UUID companyId, UUID userId);

    CompanyResponse uploadLogo(UUID companyId, UUID userId, MultipartFile file);

    CompanyUserResponse addEmployee(UUID companyId, UUID managerId, AddCompanyEmployeeRequest request);

    void removeEmployee(UUID companyId, UUID managerId, UUID memberId);

    List<CompanyUserResponse> getEmployees(UUID companyId);
}
