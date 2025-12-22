package com.job.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobUpdateRequestDTO {

    @Size(max = 150)
    private String title;

    private String description;

    @Size(max = 100)
    private String location;

    @Size(max = 50)
    private String contractType;

    private Boolean active;

    private Long domainId;
}


