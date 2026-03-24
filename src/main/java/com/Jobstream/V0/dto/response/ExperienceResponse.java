package com.Jobstream.V0.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExperienceResponse {

    private UUID id;
    private UUID userId;
    private UUID companyId;
    private String companyName;
    private String title;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
