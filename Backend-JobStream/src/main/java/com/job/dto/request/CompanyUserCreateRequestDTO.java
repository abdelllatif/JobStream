package com.job.dto.request;

import com.job.enums.CompanyRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyUserCreateRequestDTO {
    @NotNull
    private Long companyId;
    
    @NotNull
    private CompanyRole companyRole;
}

