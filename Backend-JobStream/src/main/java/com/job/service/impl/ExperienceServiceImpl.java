package com.job.service.impl;

import com.job.dto.request.ExperienceCreateRequestDTO;
import com.job.dto.request.ExperienceUpdateRequestDTO;
import com.job.dto.response.ExperienceResponseDTO;
import com.job.entity.CandidateProfile;
import com.job.entity.Experience;
import com.job.mapper.ExperienceMapper;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.ExperienceRepository;
import com.job.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final ExperienceMapper experienceMapper;

    @Override
    @Transactional
    public ExperienceResponseDTO create(ExperienceCreateRequestDTO dto) {
        Experience experience = experienceMapper.toEntity(dto);

        CandidateProfile candidateProfile = candidateProfileRepository.findById(dto.getCandidateProfileId())
                .orElseThrow(() -> new RuntimeException("Candidate profile not found with id: " + dto.getCandidateProfileId()));

        experience.setCandidateProfile(candidateProfile);

        return experienceMapper.toResponse(experienceRepository.save(experience));
    }

    @Override
    public ExperienceResponseDTO getById(Long id) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + id));
        return experienceMapper.toResponse(experience);
    }

    @Override
    public List<ExperienceResponseDTO> getAll() {
        return experienceRepository.findAll().stream()
                .map(experienceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExperienceResponseDTO update(Long id, ExperienceUpdateRequestDTO dto) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + id));

        Experience updatedExperience = experienceMapper.toEntity(dto);

        if (dto.getTitle() != null) experience.setTitle(updatedExperience.getTitle());
        if (dto.getCompany() != null) experience.setCompany(updatedExperience.getCompany());
        if (dto.getStartDate() != null) experience.setStartDate(updatedExperience.getStartDate());
        if (dto.getEndDate() != null) experience.setEndDate(updatedExperience.getEndDate());
        if (dto.getDescription() != null) experience.setDescription(updatedExperience.getDescription());

        return experienceMapper.toResponse(experienceRepository.save(experience));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + id));
        experienceRepository.delete(experience);
    }
}

