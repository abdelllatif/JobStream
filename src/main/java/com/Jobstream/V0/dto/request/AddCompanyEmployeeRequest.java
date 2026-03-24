package com.Jobstream.V0.dto.request;

import com.Jobstream.V0.enums.CompanyRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AddCompanyEmployeeRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Role is required")
    private CompanyRole role;

    private LocalDate startDate;
}
