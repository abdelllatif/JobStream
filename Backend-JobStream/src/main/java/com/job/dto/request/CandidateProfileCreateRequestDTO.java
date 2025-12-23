package com.job.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CandidateProfileCreateRequestDTO {

    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String address;

    @Size(max = 1000)
    private String summary;

    @Size(max = 255)
    private String cvUrl;
}



