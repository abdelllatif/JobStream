package com.Jobstream.V0.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CompanyResponse {

    private UUID id;
    private String name;
    private String description;
    private String logoUrl;
    private String website;
    private String location;
    private String domain;
    private UUID createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int employeeCount;
}
