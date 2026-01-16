package com.job.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String contractType;
    private LocalDateTime postedAt;
    private LocalDateTime updatedAt;
    private boolean active;

    private Long companyId;
    private Long domainId;
    private List<Long> tagIds;
}



