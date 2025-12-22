package com.job.dto.request;

import com.job.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationUpdateRequestDTO {

    private ApplicationStatus status;
}


