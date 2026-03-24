package com.job.dto.response;

import lombok.Data;

@Data
public class CandidateProfileResponseDTO {
    private Long id;
    private Long userId;
    private String phone;
    private String address;
    private String summary;
    private String jobTitle;
    private String linkedinProfile;
    private String githubProfile;
    private String portfolioUrl;
    private String cvUrl;

    private java.util.List<ExperienceResponseDTO> experiences;
    private java.util.List<EducationResponseDTO> educations;
    private java.util.List<SkillResponseDTO> skills;
}
