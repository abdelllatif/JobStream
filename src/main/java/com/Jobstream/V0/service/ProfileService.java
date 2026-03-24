package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.ProfileRequest;
import com.Jobstream.V0.dto.response.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProfileService {

    ProfileResponse getByUserId(UUID userId);

    ProfileResponse createOrUpdate(UUID userId, ProfileRequest request);

    ProfileResponse uploadPhoto(UUID userId, MultipartFile file);

    ProfileResponse uploadCv(UUID userId, MultipartFile file);
}
