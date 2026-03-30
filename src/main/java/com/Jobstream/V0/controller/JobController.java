package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.JobRequest;
import com.Jobstream.V0.dto.response.JobResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.JobStatus;
import com.Jobstream.V0.enums.JobType;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.JobService;
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
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Jobs", description = "Endpoints for job postings")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @Operation(summary = "Post a new job")
    public ResponseEntity<JobResponse> postJob(
            @Valid @RequestBody JobRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.create(currentUserId(auth), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing job")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable UUID id, @Valid @RequestBody JobRequest request, Authentication auth) {
        return ResponseEntity.ok(jobService.update(id, currentUserId(auth), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a job posting by ID")
    public ResponseEntity<JobResponse> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @GetMapping("search")
    @Operation(summary = "Search internal job postings")
    public ResponseEntity<PageResponse<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) JobStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(jobService.searchJobs(keyword, location, jobType, status, pageable));
    }
    
        @GetMapping("/except-poster")
        @Operation(summary = "Get all jobs except those posted by the authenticated user")
        public ResponseEntity<List<JobResponse>> getAllJobsExceptPoster(Authentication auth) {
            return ResponseEntity.ok(jobService.getAllJobsExceptPoster(currentUserId(auth)));
        }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get all open jobs for a specific company")
    public ResponseEntity<List<JobResponse>> getCompanyJobs(@PathVariable UUID companyId) {
        return ResponseEntity.ok(jobService.getByCompany(companyId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job posting")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id, Authentication auth) {
        jobService.delete(id, currentUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
