package com.job.service;

import com.job.dto.request.ApplicationCreateRequestDTO;
import com.job.dto.request.ApplicationUpdateRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;

import java.util.List;

public interface ApplicationService {
    ApplicationResponseDTO create(ApplicationCreateRequestDTO dto);
    ApplicationResponseDTO getById(Long id);
    List<ApplicationResponseDTO> getAll();
    ApplicationResponseDTO update(Long id, ApplicationUpdateRequestDTO dto);
    void delete(Long id);
}

