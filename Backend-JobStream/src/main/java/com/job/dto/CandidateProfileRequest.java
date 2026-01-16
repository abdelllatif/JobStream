package com.job.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CandidateProfileRequest {
    
    @Size(max = 500, message = "Headline must not exceed 500 characters")
    private String headline;
    
    @Size(max = 2000, message = "Summary must not exceed 2000 characters")
    private String summary;
    
    private String cvFilePath;
    
    private String linkedinProfile;
    
    private String githubProfile;
    
    private String portfolioUrl;
    
    private java.util.List<String> skills;
    
    private java.util.List<ExperienceRequest> experiences;
    
    private java.util.List<EducationRequest> educations;
    
    private Long userId;
    
    @Data
    public static class ExperienceRequest {
        private String company;
        private String position;
        private String description;
        private String startDate;
        private String endDate;
        private Boolean current;
    }
    
    @Data
    public static class EducationRequest {
        private String institution;
        private String degree;
        private String field;
        private String startDate;
        private String endDate;
        private String description;
    }
}
