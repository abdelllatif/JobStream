package com.job.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationUpdateRequestDTO {

    private String school;
    private String degree;
    private LocalDate startDate;
    private LocalDate endDate;
}


