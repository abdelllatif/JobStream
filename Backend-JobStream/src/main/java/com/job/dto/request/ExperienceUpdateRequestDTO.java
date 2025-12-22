package com.job.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExperienceUpdateRequestDTO {

    private String title;
    private String company;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}



