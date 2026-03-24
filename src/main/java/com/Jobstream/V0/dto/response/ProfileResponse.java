package com.Jobstream.V0.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProfileResponse {

    private UUID id;
    private UUID userId;
    private String headline;
    private String bio;
    private String location;
    private String photoUrl;
    private String cvUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String websiteUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
