package com.job.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GlobalSearchResponseDTO {
    private List<UserResponseDTO> users;
    private List<JobResponseDTO> jobs;
    private List<CompanyResponseDTO> companies;
}
