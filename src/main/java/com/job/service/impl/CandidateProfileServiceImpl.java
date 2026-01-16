package com.job.service.impl;

import com.job.dto.request.CandidateProfileCreateRequestDTO;
import com.job.dto.request.CandidateProfileUpdateRequestDTO;
import com.job.dto.response.CandidateProfileResponseDTO;
import com.job.entity.CandidateProfile;
import com.job.entity.User;
import com.job.exception.UserNotFoundException;
import com.job.mapper.CandidateProfileMapper;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.UserRepository;
import com.job.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final CandidateProfileMapper candidateProfileMapper;

    @Override
    @Transactional
    public CandidateProfileResponseDTO create(CandidateProfileCreateRequestDTO dto) {
        CandidateProfile candidateProfile = candidateProfileMapper.toEntity(dto);

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + dto.getUserId()));

        candidateProfile.setUser(user);

        return candidateProfileMapper.toResponse(candidateProfileRepository.save(candidateProfile));
    }

    @Override
    public CandidateProfileResponseDTO getById(Long id) {
        CandidateProfile candidateProfile = candidateProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found with id: " + id));
        return candidateProfileMapper.toResponse(candidateProfile);
    }

    @Override
    public List<CandidateProfileResponseDTO> getAll() {
        return candidateProfileRepository.findAll().stream()
                .map(candidateProfileMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CandidateProfileResponseDTO update(Long id, CandidateProfileUpdateRequestDTO dto) {
        CandidateProfile candidateProfile = candidateProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found with id: " + id));

        CandidateProfile updatedProfile = candidateProfileMapper.toEntity(dto);

        if (dto.getPhone() != null) candidateProfile.setPhone(updatedProfile.getPhone());
        if (dto.getAddress() != null) candidateProfile.setAddress(updatedProfile.getAddress());
        if (dto.getSummary() != null) candidateProfile.setSummary(updatedProfile.getSummary());
        if (dto.getCvUrl() != null) candidateProfile.setCvUrl(updatedProfile.getCvUrl());

        return candidateProfileMapper.toResponse(candidateProfileRepository.save(candidateProfile));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CandidateProfile candidateProfile = candidateProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found with id: " + id));
        candidateProfileRepository.delete(candidateProfile);
    }

    public CandidateProfile getEntityById(Long id){
        return candidateProfileRepository.findById(id).orElse(null);
    }
}

