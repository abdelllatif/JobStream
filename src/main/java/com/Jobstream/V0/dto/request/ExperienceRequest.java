package com.Jobstream.V0.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExperienceRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private UUID companyId;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;
    private String description;
}
