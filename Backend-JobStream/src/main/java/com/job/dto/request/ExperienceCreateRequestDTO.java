package com.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExperienceCreateRequestDTO {

    @NotNull
    private Long candidateProfileId;

    @NotBlank
    private String title;

    @NotBlank
    private String company;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}



