package com.job.service.impl;

import com.job.dto.request.CompanyCreateRequestDTO;
import com.job.dto.request.CompanyUpdateRequestDTO;
import com.job.dto.response.CompanyResponseDTO;
import com.job.entity.Company;
import com.job.entity.User;
import com.job.entity.CompanyUser;
import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import com.job.exception.CompanyNotFoundException;
import com.job.exception.UserNotFoundException;
import com.job.mapper.CompanyMapper;
import com.job.repository.CompanyRepository;
import com.job.repository.CompanyUserRepository;
import com.job.repository.UserRepository;
import com.job.service.CompanyService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyMapper companyMapper;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public CompanyResponseDTO create(CompanyCreateRequestDTO dto) {
        Company company = companyMapper.toEntity(dto);
        
        Long userId = authUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User must be authenticated to create a company");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        company.setOwner(user);
        
        Company savedCompany = companyRepository.save(company);

        // Auto-create CompanyUser as CEO
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompany(savedCompany);
        companyUser.setUser(user);
        companyUser.setRole(CompanyRole.CEO);
        companyUser.setStatus(MembershipStatus.ACTIVE);
        companyUser.setJoinedAt(LocalDate.now());
        companyUser.setJobTitle("Founder & CEO");
        companyUserRepository.save(companyUser);

        return companyMapper.toResponse(savedCompany);
    }

    @Override
    public CompanyResponseDTO getById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + id));
        return companyMapper.toResponse(company);
    }

    @Override
    public List<CompanyResponseDTO> getAll() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyResponseDTO update(Long id, CompanyUpdateRequestDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + id));

        Company updatedCompany = companyMapper.toEntity(dto);

        if (dto.getName() != null) company.setName(updatedCompany.getName());
        if (dto.getDescription() != null) company.setDescription(updatedCompany.getDescription());
        if (dto.getWebsite() != null) company.setWebsite(updatedCompany.getWebsite());
        if (dto.getLogoUrl() != null) company.setLogoUrl(updatedCompany.getLogoUrl());

        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + id));
        companyRepository.delete(company);
    }
}

