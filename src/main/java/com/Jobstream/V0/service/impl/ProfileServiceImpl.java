package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ProfileRequest;
import com.Jobstream.V0.dto.response.ProfileResponse;
import com.Jobstream.V0.entity.Profile;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.mapper.ProfileMapper;
import com.Jobstream.V0.repository.ProfileRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.FileStorageService;
import com.Jobstream.V0.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getByUserId(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));
        return ProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponse createOrUpdate(UUID userId, ProfileRequest request) {
        User user = findUser(userId);
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> Profile.builder().user(user).build());

        applyProfileRequest(profile, request);
        return ProfileMapper.toResponse(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public ProfileResponse uploadPhoto(UUID userId, MultipartFile file) {
        User user = findUser(userId);
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> Profile.builder().user(user).build());

        String photoUrl = fileStorageService.storeFile(file, "photos");
        profile.setPhotoUrl(photoUrl);
        return ProfileMapper.toResponse(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public ProfileResponse uploadCv(UUID userId, MultipartFile file) {
        User user = findUser(userId);
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> Profile.builder().user(user).build());

        String cvUrl = fileStorageService.storeFile(file, "cvs");
        profile.setCvUrl(cvUrl);
        return ProfileMapper.toResponse(profileRepository.save(profile));
    }

    private void applyProfileRequest(Profile profile, ProfileRequest request) {
        if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getWebsiteUrl() != null) profile.setWebsiteUrl(request.getWebsiteUrl());
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
