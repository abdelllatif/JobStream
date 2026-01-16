package com.job.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ApplicationRequest {
    
    @NotBlank(message = "Job ID is required")
    private Long jobId;
    
    @NotBlank(message = "Candidate ID is required")
    private Long candidateId;
    
    @Size(max = 2000, message = "Cover letter must not exceed 2000 characters")
    private String coverLetter;
    
    private String cvFilePath;
}
