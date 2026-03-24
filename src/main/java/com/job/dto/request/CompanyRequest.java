package com.job.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CompanyRequest {
    
    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private String industry;
    
    private String website;
    
    private String logo;
    
    private String address;
    
    private String city;
    
    private String country;
    
    private String postalCode;
    
    private String phone;
    
    private Integer size;
    
    private Long userId;
}
