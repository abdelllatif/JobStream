package com.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CandidateProfileCreateRequestDTO {

    @NotBlank
    @Size(max = 20)
    private String phone;

    private Long userId;

    @Size(max = 255)
    private String address;

    @Size(max = 1000)
    private String summary;

    @Size(max = 255)
    private String cvUrl;

    @Size(max = 255)
    private String jobTitle;

    @Size(max = 255)
    private String linkedinProfile;

    @Size(max = 255)
    private String githubProfile;

    @Size(max = 255)
    private String portfolioUrl;
}
