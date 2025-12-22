package com.job.service;

import com.job.dto.request.CandidateProfileCreateRequestDTO;
import com.job.dto.request.CandidateProfileUpdateRequestDTO;
import com.job.dto.response.CandidateProfileResponseDTO;

import java.util.List;

public interface CandidateProfileService {
    CandidateProfileResponseDTO create(CandidateProfileCreateRequestDTO dto);
    CandidateProfileResponseDTO getById(Long id);
    List<CandidateProfileResponseDTO> getAll();
    CandidateProfileResponseDTO update(Long id, CandidateProfileUpdateRequestDTO dto);
    void delete(Long id);
}

