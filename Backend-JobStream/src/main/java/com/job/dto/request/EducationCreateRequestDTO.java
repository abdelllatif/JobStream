package com.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationCreateRequestDTO {

    @NotNull
    private Long candidateProfileId;

    @NotBlank
    private String school;

    @NotBlank
    private String degree;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;
}


