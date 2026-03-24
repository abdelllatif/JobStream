package com.Jobstream.V0.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SkillResponse {

    private UUID id;
    private UUID userId;
    private String name;
    private LocalDateTime createdAt;
}
