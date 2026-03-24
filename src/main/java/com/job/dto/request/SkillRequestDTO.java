package com.job.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
public class SkillRequestDTO {
    @NotBlank
    private String name;

    @Min(1)
    @Max(100)
    private int level;
}
