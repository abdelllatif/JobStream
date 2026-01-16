package com.job.service.impl;

import com.job.dto.request.JobCreateRequestDTO;
import com.job.dto.request.JobUpdateRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.entity.Company;
import com.job.entity.Domain;
import com.job.entity.Job;
import com.job.exception.CompanyNotFoundException;
import com.job.exception.JobNotFoundException;
import com.job.mapper.JobMapper;
import com.job.repository.CompanyRepository;
import com.job.repository.DomainRepository;
import com.job.repository.JobRepository;
import com.job.service.JobService;
import com.job.util.TagExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final DomainRepository domainRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public JobResponseDTO create(JobCreateRequestDTO dto) {
        Job job = jobMapper.toEntity(dto);

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + dto.getCompanyId()));

        Domain domain = domainRepository.findById(dto.getDomainId())
                .orElseThrow(() -> new RuntimeException("Domain not found with id: " + dto.getDomainId()));

        job.setCompany(company);
        job.setDomain(domain);
        job.setPostedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setActive(true);

        // Extraire les tags de la description
        String description = dto.getDescription();
        if (TagExtractor.hasTags(description)) {
            String tags = TagExtractor.extractTagsAsString(description);
            // Vous pouvez stocker les tags dans un champ séparé si nécessaire
            // job.setTags(tags);
            System.out.println("Tags extraits de la description: " + tags);
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

        Job updatedJob = jobMapper.toEntity(dto);
        
        if (dto.getDomainId() != null) {
            Domain domain = domainRepository.findById(dto.getDomainId())
                    .orElseThrow(() -> new RuntimeException("Domain not found with id: " + dto.getDomainId()));
            job.setDomain(domain);
        }

        if (dto.getTitle() != null) job.setTitle(updatedJob.getTitle());
        if (dto.getDescription() != null) job.setDescription(updatedJob.getDescription());
        if (dto.getLocation() != null) job.setLocation(updatedJob.getLocation());
        if (dto.getContractType() != null) job.setContractType(updatedJob.getContractType());
        if (dto.getActive() != null) job.setActive(dto.getActive());
        
        job.setUpdatedAt(LocalDateTime.now());

        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
        jobRepository.delete(job);
    }
}

