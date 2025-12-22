package com.job.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CandidateProfileUpdateRequestDTO {

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String address;

    @Size(max = 1000)
    private String summary;

    @Size(max = 255)
    private String cvUrl;
}



