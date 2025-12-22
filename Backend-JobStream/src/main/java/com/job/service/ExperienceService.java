package com.job.service;

import com.job.dto.request.ExperienceCreateRequestDTO;
import com.job.dto.request.ExperienceUpdateRequestDTO;
import com.job.dto.response.ExperienceResponseDTO;

import java.util.List;

public interface ExperienceService {
    ExperienceResponseDTO create(ExperienceCreateRequestDTO dto);
    ExperienceResponseDTO getById(Long id);
    List<ExperienceResponseDTO> getAll();
    ExperienceResponseDTO update(Long id, ExperienceUpdateRequestDTO dto);
    void delete(Long id);
}

