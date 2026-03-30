package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.AddCompanyEmployeeRequest;
import com.Jobstream.V0.dto.request.CompanyRequest;
import com.Jobstream.V0.dto.response.CompanyResponse;
import com.Jobstream.V0.dto.response.CompanyUserResponse;
import com.Jobstream.V0.entity.Company;
import com.Jobstream.V0.entity.CompanyUser;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.CompanyRole;
import com.Jobstream.V0.exception.BadRequestException;
import com.Jobstream.V0.exception.DuplicateResourceException;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.CompanyMapper;
import com.Jobstream.V0.repository.CompanyRepository;
import com.Jobstream.V0.repository.CompanyUserRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.CompanyService;
import com.Jobstream.V0.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public CompanyResponse create(UUID userId, CompanyRequest request) {
        User user = findUser(userId);
        Company company = Company.builder()
                .name(request.getName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .location(request.getLocation())
                .domain(request.getDomain())
                .createdBy(user)
                .build();
        company = companyRepository.save(company);

        CompanyUser ownerEntry = CompanyUser.builder()
                .company(company).user(user).role(CompanyRole.OWNER).isCurrent(true).build();
        companyUserRepository.save(ownerEntry);

        return CompanyMapper.toResponse(company);
    }

    @Override
    @Transactional
    public CompanyResponse update(UUID companyId, UUID userId, CompanyRequest request) {
        Company company = findCompany(companyId);
        assertOwner(companyId, userId);

        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());
        company.setDomain(request.getDomain());
        return CompanyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getById(UUID companyId) {
        return CompanyMapper.toResponse(findCompany(companyId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponse> search(String query, Pageable pageable) {
        return companyRepository.searchByName(query, pageable).map(CompanyMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getMyCompanies(UUID userId) {
        return companyRepository.findCompaniesByUserId(userId)
                .stream().map(CompanyMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID companyId, UUID userId) {
        Company company = findCompany(companyId);
        if (!company.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("Only company owner can delete it");
        }
        companyRepository.delete(company);
    }

    @Override
    @Transactional
    public CompanyResponse uploadLogo(UUID companyId, UUID userId, MultipartFile file) {
        Company company = findCompany(companyId);
        assertOwner(companyId, userId);
        String logoUrl = fileStorageService.storeFile(file, "logos");
        company.setLogoUrl(logoUrl);
        return CompanyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyUserResponse addEmployee(UUID companyId, UUID managerId, AddCompanyEmployeeRequest request) {
        assertOwner(companyId, managerId);
        Company company = findCompany(companyId);
        User user = findUser(request.getUserId());

        if (companyUserRepository.existsByCompanyIdAndUserId(companyId, request.getUserId())) {
            throw new DuplicateResourceException("User is already a member of this company");
        }

        CompanyUser cu = CompanyUser.builder()
                .company(company).user(user).role(CompanyRole.OWNER)
                .startDate(request.getStartDate()).isCurrent(true).build();
        return CompanyMapper.toEmployeeResponse(companyUserRepository.save(cu));
    }

    @Override
    @Transactional
    public void removeEmployee(UUID companyId, UUID managerId, UUID memberId) {
        assertOwner(companyId, managerId);
        if (managerId.equals(memberId)) {
            throw new BadRequestException("Cannot remove yourself as owner");
        }
        CompanyUser cu = companyUserRepository.findByCompanyIdAndUserId(companyId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        companyUserRepository.delete(cu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyUserResponse> getEmployees(UUID companyId) {
        return companyUserRepository.findByCompanyId(companyId)
                .stream().map(CompanyMapper::toEmployeeResponse).collect(Collectors.toList());
    }

    private void assertOwner(UUID companyId, UUID userId) {
        companyUserRepository.findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> new UnauthorizedException("Only company owners can perform this action"));
    }

    private Company findCompany(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
