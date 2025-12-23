package com.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobCreateRequestDTO {

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    @Size(max = 100)
    private String location;

    @NotBlank
    @Size(max = 50)
    private String contractType;

    @NotNull
    private Long companyId;

    @NotNull
    private Long domainId;
}



