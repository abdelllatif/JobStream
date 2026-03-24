package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ExperienceRequest;
import com.Jobstream.V0.dto.response.ExperienceResponse;
import com.Jobstream.V0.entity.Company;
import com.Jobstream.V0.entity.Experience;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.mapper.ExperienceMapper;
import com.Jobstream.V0.repository.CompanyRepository;
import com.Jobstream.V0.repository.ExperienceRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponse> getByUserId(UUID userId) {
        return experienceRepository.findByUserIdOrderByStartDateDesc(userId)
                .stream().map(ExperienceMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExperienceResponse create(UUID userId, ExperienceRequest request) {
        User user = findUser(userId);
        Company company = resolveCompany(request.getCompanyId());

        Experience experience = Experience.builder()
                .user(user)
                .company(company)
                .title(request.getTitle())
                .employmentType(request.getEmploymentType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isCurrent(request.isCurrent())
                .description(request.getDescription())
                .build();

        return ExperienceMapper.toResponse(experienceRepository.save(experience));
    }

    @Override
    @Transactional
    public ExperienceResponse update(UUID experienceId, UUID userId, ExperienceRequest request) {
        Experience experience = findOwnedExperience(experienceId, userId);
        experience.setCompany(resolveCompany(request.getCompanyId()));
        experience.setTitle(request.getTitle());
        experience.setEmploymentType(request.getEmploymentType());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setCurrent(request.isCurrent());
        experience.setDescription(request.getDescription());
        return ExperienceMapper.toResponse(experienceRepository.save(experience));
    }

    @Override
    @Transactional
    public void delete(UUID experienceId, UUID userId) {
        Experience experience = findOwnedExperience(experienceId, userId);
        experienceRepository.delete(experience);
    }

    private Company resolveCompany(UUID companyId) {
        if (companyId == null) return null;
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
    }

    private Experience findOwnedExperience(UUID experienceId, UUID userId) {
        return experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience", "id", experienceId));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
