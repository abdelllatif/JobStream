package com.job.service;

import com.job.dto.request.JobCreateRequestDTO;
import com.job.dto.request.JobUpdateRequestDTO;
import com.job.dto.response.JobResponseDTO;

import java.util.List;

public interface JobService {
    JobResponseDTO create(JobCreateRequestDTO dto);
    JobResponseDTO getById(Long id);
    List<JobResponseDTO> getAll();
    JobResponseDTO update(Long id, JobUpdateRequestDTO dto);
    void delete(Long id);
}

