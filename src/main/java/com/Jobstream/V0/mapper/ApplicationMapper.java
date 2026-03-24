package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.ApplicationResponse;
import com.Jobstream.V0.entity.Application;

public class ApplicationMapper {

    public static ApplicationResponse toResponse(Application application) {
        String headline = application.getUser().getProfile() != null
                ? application.getUser().getProfile().getHeadline() : null;
        String photoUrl = application.getUser().getProfile() != null
                ? application.getUser().getProfile().getPhotoUrl() : null;

        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompany().getName())
                .userId(application.getUser().getId())
                .userEmail(application.getUser().getEmail())
                .userHeadline(headline)
                .userPhotoUrl(photoUrl)
                .cvUrl(application.getCvUrl())
                .status(application.getStatus())
                .coverLetter(application.getCoverLetter())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    private ApplicationMapper() {}
}
