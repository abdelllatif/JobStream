package com.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyCreateRequestDTO {

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 255)
    private String website;

    @Size(max = 255)
    private String logoUrl;

    // userId is usually inferred from auth, but kept here if you need explicit linking
    private Long userId;
}


