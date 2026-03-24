package com.Jobstream.V0.dto.response;

import com.Jobstream.V0.enums.CompanyRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CompanyUserResponse {

    private UUID id;
    private UUID userId;
    private String userEmail;
    private String userHeadline;
    private String userPhotoUrl;
    private UUID companyId;
    private String companyName;
    private CompanyRole role;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;
    private LocalDateTime createdAt;
}
