package com.job.service;

import com.job.dto.request.EducationCreateRequestDTO;
import com.job.dto.request.EducationUpdateRequestDTO;
import com.job.dto.response.EducationResponseDTO;

import java.util.List;

public interface EducationService {
    EducationResponseDTO create(EducationCreateRequestDTO dto);
    EducationResponseDTO getById(Long id);
    List<EducationResponseDTO> getAll();
    EducationResponseDTO update(Long id, EducationUpdateRequestDTO dto);
    void delete(Long id);
}

