package com.job.service;

import com.job.dto.request.CompanyCreateRequestDTO;
import com.job.dto.request.CompanyUpdateRequestDTO;
import com.job.dto.response.CompanyResponseDTO;

import java.util.List;

public interface CompanyService {
    CompanyResponseDTO create(CompanyCreateRequestDTO dto);
    CompanyResponseDTO getById(Long id);
    List<CompanyResponseDTO> getAll();
    CompanyResponseDTO update(Long id, CompanyUpdateRequestDTO dto);
    void delete(Long id);
}

