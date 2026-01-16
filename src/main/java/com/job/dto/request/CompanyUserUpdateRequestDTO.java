package com.job.dto.request;

import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import lombok.Data;

@Data
public class CompanyUserUpdateRequestDTO {
    private String JobTitle;
    private MembershipStatus status;
}

