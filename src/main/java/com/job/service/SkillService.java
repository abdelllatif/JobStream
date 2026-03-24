package com.job.service;

import com.job.dto.request.SkillRequestDTO;
import com.job.dto.response.SkillResponseDTO;

import java.util.List;

public interface SkillService {
    SkillResponseDTO create(SkillRequestDTO dto);

    SkillResponseDTO getById(Long id);

    List<SkillResponseDTO> getAll();

    SkillResponseDTO update(Long id, SkillRequestDTO dto);

    void delete(Long id);
}
