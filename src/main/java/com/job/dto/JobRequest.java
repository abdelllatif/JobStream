package com.job.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class JobRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 5000, message = "Description must be between 20 and 5000 characters")
    private String description;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "Contract type is required")
    private String contractType;
    
    private Long companyId;
    private Long domainId;
    private Double salary;
    private String experienceLevel;
    private Boolean remote;
    private java.util.List<Long> tagIds;
}
