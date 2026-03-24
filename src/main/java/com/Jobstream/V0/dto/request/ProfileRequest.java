package com.Jobstream.V0.dto.request;

import lombok.Data;

@Data
public class ProfileRequest {

    private String headline;
    private String bio;
    private String location;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String websiteUrl;
}
