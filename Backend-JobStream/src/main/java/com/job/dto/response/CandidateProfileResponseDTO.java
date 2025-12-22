package com.job.dto.response;

import lombok.Data;

@Data
public class CandidateProfileResponseDTO {
    private Long id;
    private Long userId;
    private String phone;
    private String address;
    private String summary;
    private String cvUrl;
}



