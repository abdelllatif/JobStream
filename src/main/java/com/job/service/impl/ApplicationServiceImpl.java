package com.job.service.impl;

import com.job.dto.request.ApplicationCreateRequestDTO;
import com.job.dto.request.ApplicationUpdateRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;
import com.job.entity.Application;
import com.job.entity.CandidateProfile;
import com.job.entity.Job;
import com.job.enums.ApplicationStatus;
import com.job.exception.ApplicationNotFoundException;
import com.job.exception.JobNotFoundException;
import com.job.mapper.ApplicationMapper;
import com.job.repository.ApplicationRepository;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.JobRepository;
import com.job.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobRepository jobRepository;
    private final ApplicationMapper applicationMapper;

    @Override
    @Transactional
    public ApplicationResponseDTO create(ApplicationCreateRequestDTO dto) {
        Application application = applicationMapper.toEntity(dto);

        CandidateProfile candidateProfile = candidateProfileRepository.findById(dto.getCandidateProfileId())
                .orElseThrow(() -> new RuntimeException("Candidate profile not found with id: " + dto.getCandidateProfileId()));

        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + dto.getJobId()));

        application.setCandidateProfile(candidateProfile);
        application.setJob(job);
        application.setStatus(dto.getStatus() != null ? dto.getStatus() : ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Override
    public ApplicationResponseDTO getById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));
        return applicationMapper.toResponse(application);
    }

    @Override
    public List<ApplicationResponseDTO> getAll() {
        return applicationRepository.findAll().stream()
                .map(applicationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponseDTO update(Long id, ApplicationUpdateRequestDTO dto) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));

        if (dto.getStatus() != null) {
            application.setStatus(dto.getStatus());
        }

        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));
        applicationRepository.delete(application);
    }
}

