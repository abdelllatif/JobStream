package com.job.service.impl;

import com.job.dto.request.JobCreateRequestDTO;
import com.job.dto.request.JobUpdateRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.entity.*;
import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import com.job.exception.CompanyNotFoundException;
import com.job.exception.JobNotFoundException;
import com.job.mapper.JobMapper;
import com.job.repository.CompanyRepository;
import com.job.repository.CompanyUserRepository;
import com.job.repository.DomainRepository;
import com.job.repository.JobRepository;
import com.job.service.JobService;
import com.job.util.AuthUtil;
import com.job.util.TagExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final DomainRepository domainRepository;
    private final JobMapper jobMapper;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public JobResponseDTO create(JobCreateRequestDTO dto) {
        validateUserCanManageCompanyJobs(dto.getCompanyId());
        
        Job job = jobMapper.toEntity(dto);

        Domain domain = domainRepository.findById(dto.getDomainId())
                .orElseThrow(() -> new RuntimeException("Domain not found with id: " + dto.getDomainId()));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + dto.getCompanyId()));

        job.setCompany(company);
        job.setDomain(domain);
        job.setPostedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setActive(true);
        job.setExternalLink(dto.getExternalLink());

        // Extraire les tags de la description
        String description = dto.getDescription();
        if (TagExtractor.hasTags(description)) {
            String tags = TagExtractor.extractTagsAsString(description);
            // Vous pouvez stocker les tags dans un champ séparé si nécessaire
            // job.setTags(tags);
            log.debug("Tags extraits de la description: {}", tags);
        }

        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    public JobResponseDTO getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
        return jobMapper.toResponse(job);
    }

    @Override
    public List<JobResponseDTO> getAll() {
        return jobRepository.findAll().stream()
                .map(jobMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobResponseDTO update(Long id, JobUpdateRequestDTO dto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        validateUserCanManageCompanyJobs(job.getCompany().getId());

        Job updatedJob = jobMapper.toEntity(dto);

        if (dto.getDomainId() != null) {
            Domain domain = domainRepository.findById(dto.getDomainId())
                    .orElseThrow(() -> new RuntimeException("Domain not found with id: " + dto.getDomainId()));
            job.setDomain(domain);
        }

        if (dto.getTitle() != null)
            job.setTitle(updatedJob.getTitle());
        if (dto.getDescription() != null)
            job.setDescription(updatedJob.getDescription());
        if (dto.getLocation() != null)
            job.setLocation(updatedJob.getLocation());
        if (dto.getContractType() != null)
            job.setContractType(updatedJob.getContractType());
        if (dto.getActive() != null)
            job.setActive(dto.getActive());
        if (dto.getExternalLink() != null)
            job.setExternalLink(dto.getExternalLink());

        job.setUpdatedAt(LocalDateTime.now());

        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
        
        validateUserCanManageCompanyJobs(job.getCompany().getId());
        
        jobRepository.delete(job);
    }

    private void validateUserCanManageCompanyJobs(Long companyId) {
        User currentUser = authUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User must be authenticated");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + companyId));

        CompanyUser membership = companyUserRepository.findByUserAndCompany(currentUser, company)
                .orElseThrow(() -> new RuntimeException("User is not a member of this company"));

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new RuntimeException("Company membership is not active. Current status: " + membership.getStatus());
        }

        CompanyRole role = membership.getRole();
        if (role != CompanyRole.CEO && role != CompanyRole.HR && role != CompanyRole.RECRUITER) {
            throw new RuntimeException("User does not have permission to manage jobs. Role: " + role);
        }
    }

    @Override
    public List<JobResponseDTO> getJobsByCompanyId(Long companyId) {
        return jobRepository.findByCompanyId(companyId).stream()
                .map(jobMapper::toResponse)
                .collect(Collectors.toList());
    }
}
