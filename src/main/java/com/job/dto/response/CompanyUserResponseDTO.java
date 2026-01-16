package com.job.dto.response;

import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompanyUserResponseDTO {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private Long companyId;
    private String companyName;
    private CompanyRole companyRole;
    private LocalDate joinedAt;
    private MembershipStatus status;
}

