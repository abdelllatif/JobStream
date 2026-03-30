package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.ApplicationRequest;
import com.Jobstream.V0.dto.request.UpdateApplicationStatusRequest;
import com.Jobstream.V0.dto.response.ApplicationResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Applications", description = "Endpoints for job applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Apply to a job")
    public ResponseEntity<ApplicationResponse> apply(
            @Valid @RequestBody ApplicationRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.apply(currentUserId(auth), request));
    }

    @GetMapping("/my")
    @Operation(summary = "Get applications submitted by current user")
    public ResponseEntity<PageResponse<ApplicationResponse>> getMyApplications(
            Authentication auth, Pageable pageable) {
        return ResponseEntity.ok(applicationService.getMyApplications(currentUserId(auth), pageable));
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Get all applications for a specific job (company members only)")
    public ResponseEntity<List<ApplicationResponse>> getJobApplications(
            @PathVariable UUID jobId, Authentication auth) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId, currentUserId(auth)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update application status (company members only)")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            Authentication auth) {
        return ResponseEntity.ok(applicationService.updateStatus(id, currentUserId(auth), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Withdraw an application")
    public ResponseEntity<Void> withdrawApplication(@PathVariable UUID id, Authentication auth) {
        applicationService.withdraw(id, currentUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
