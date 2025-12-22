package com.job.dto.response;

import lombok.Data;

@Data
public class CompanyResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String website;
    private String logoUrl;
    private Long userId;
}


