package com.Jobstream.V0.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AddCompanyEmployeeRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    private LocalDate startDate;
}
