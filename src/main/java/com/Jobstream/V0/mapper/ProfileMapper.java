package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.ProfileResponse;
import com.Jobstream.V0.entity.Profile;

public class ProfileMapper {

    public static ProfileResponse toResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .headline(profile.getHeadline())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .photoUrl(profile.getPhotoUrl())
                .cvUrl(profile.getCvUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .portfolioUrl(profile.getPortfolioUrl())
                .websiteUrl(profile.getWebsiteUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    private ProfileMapper() {}
}
