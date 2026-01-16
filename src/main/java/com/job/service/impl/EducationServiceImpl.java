package com.job.service.impl;

import com.job.dto.request.EducationCreateRequestDTO;
import com.job.dto.request.EducationUpdateRequestDTO;
import com.job.dto.response.EducationResponseDTO;
import com.job.entity.CandidateProfile;
import com.job.entity.Education;
import com.job.mapper.EducationMapper;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.EducationRepository;
import com.job.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final EducationMapper educationMapper;

    @Override
    @Transactional
    public EducationResponseDTO create(EducationCreateRequestDTO dto) {
        Education education = educationMapper.toEntity(dto);

        CandidateProfile candidateProfile = candidateProfileRepository.findById(dto.getCandidateProfileId())
                .orElseThrow(() -> new RuntimeException("Candidate profile not found with id: " + dto.getCandidateProfileId()));

        education.setCandidateProfile(candidateProfile);

        return educationMapper.toResponse(educationRepository.save(education));
    }

    @Override
    public EducationResponseDTO getById(Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found with id: " + id));
        return educationMapper.toResponse(education);
    }

    @Override
    public List<EducationResponseDTO> getAll() {
        return educationRepository.findAll().stream()
                .map(educationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EducationResponseDTO update(Long id, EducationUpdateRequestDTO dto) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found with id: " + id));

        Education updatedEducation = educationMapper.toEntity(dto);

        if (dto.getSchool() != null) education.setSchool(updatedEducation.getSchool());
        if (dto.getDegree() != null) education.setDegree(updatedEducation.getDegree());
        if (dto.getStartDate() != null) education.setStartDate(updatedEducation.getStartDate());
        if (dto.getEndDate() != null) education.setEndDate(updatedEducation.getEndDate());

        return educationMapper.toResponse(educationRepository.save(education));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found with id: " + id));
        educationRepository.delete(education);
    }
}

