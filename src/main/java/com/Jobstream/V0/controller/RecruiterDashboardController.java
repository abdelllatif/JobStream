package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.response.RecruiterDashboardResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.RecruiterDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Recruiter Dashboard", description = "Endpoints for recruiter statistics and dashboard")
public class RecruiterDashboardController {

    private final RecruiterDashboardService dashboardService;

    @GetMapping("/stats/company/{companyId}")
    @Operation(summary = "Get recruitment statistics for a company")
    public ResponseEntity<RecruiterDashboardResponse> getDashboardStats(
            @PathVariable UUID companyId, Authentication auth) {
        return ResponseEntity.ok(dashboardService.getDashboard(companyId, currentUserId(auth)));
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
