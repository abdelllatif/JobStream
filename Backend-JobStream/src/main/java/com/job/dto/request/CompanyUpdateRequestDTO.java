package com.job.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyUpdateRequestDTO {

    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 255)
    private String website;

    @Size(max = 255)
    private String logoUrl;
}



