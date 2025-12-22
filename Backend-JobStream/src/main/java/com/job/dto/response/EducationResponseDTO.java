package com.job.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationResponseDTO {
    private Long id;
    private Long candidateProfileId;
    private String school;
    private String degree;
    private LocalDate startDate;
    private LocalDate endDate;
}


