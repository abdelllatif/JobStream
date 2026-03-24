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
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final CandidateProfileMapper candidateProfileMapper;
    private final AuthUtil authUtil;

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

        if (dto.getPhone() != null)
            candidateProfile.setPhone(updatedProfile.getPhone());
        if (dto.getAddress() != null)
            candidateProfile.setAddress(updatedProfile.getAddress());
        if (dto.getSummary() != null)
            candidateProfile.setSummary(updatedProfile.getSummary());
        if (dto.getCvUrl() != null)
            candidateProfile.setCvUrl(updatedProfile.getCvUrl());

        if (dto.getJobTitle() != null)
            candidateProfile.setJobTitle(dto.getJobTitle());
        if (dto.getLinkedinProfile() != null)
            candidateProfile.setLinkedinProfile(dto.getLinkedinProfile());
        if (dto.getGithubProfile() != null)
            candidateProfile.setGithubProfile(dto.getGithubProfile());
        if (dto.getPortfolioUrl() != null)
            candidateProfile.setPortfolioUrl(dto.getPortfolioUrl());

        return candidateProfileMapper.toResponse(candidateProfileRepository.save(candidateProfile));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CandidateProfile candidateProfile = candidateProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found with id: " + id));
        candidateProfileRepository.delete(candidateProfile);
    }

    @Override
    public CandidateProfile getEntityById(Long id) {
        return candidateProfileRepository.findById(id).orElse(null);
    }

    @Override
    public CandidateProfile getEntityByUserId(Long userId) {
        return candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found for user id: " + userId));
    }

    @Override
    @Transactional
    public void updateCvUrl(Long userId, String cvUrl) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found for user id: " + userId));
        profile.setCvUrl(cvUrl);
        candidateProfileRepository.save(profile);
    }

    @Override
    public CandidateProfileResponseDTO getByUserId(Long userId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found for user id: " + userId));
        return candidateProfileMapper.toResponse(profile);
    }
}
