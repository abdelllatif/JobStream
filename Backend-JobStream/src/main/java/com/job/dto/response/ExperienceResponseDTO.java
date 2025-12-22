package com.job.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExperienceResponseDTO {
    private Long id;
    private Long candidateProfileId;
    private String title;
    private String company;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}


