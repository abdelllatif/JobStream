package com.job.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationCreateRequestDTO {

    @NotNull
    private Long candidateProfileId;

    @NotNull
    private Long jobId;

   
}



