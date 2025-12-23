package com.job.service.impl;

import com.job.dto.request.CompanyCreateRequestDTO;
import com.job.dto.request.CompanyUpdateRequestDTO;
import com.job.dto.response.CompanyResponseDTO;
import com.job.entity.Company;
import com.job.entity.User;
import com.job.exception.CompanyNotFoundException;
import com.job.exception.UserNotFoundException;
import com.job.mapper.CompanyMapper;
import com.job.repository.CompanyRepository;
import com.job.repository.UserRepository;
import com.job.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional
    public CompanyResponseDTO create(CompanyCreateRequestDTO dto) {
        Company company = companyMapper.toEntity(dto);

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + dto.getUserId()));
            company.setUser(user);
        }

        return companyMapper.toResponse(companyRepository.save(company));
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

