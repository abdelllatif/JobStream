package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.ExperienceRequest;
import com.Jobstream.V0.dto.response.ExperienceResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Experience", description = "Endpoints for user experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get experience by user ID")
    public ResponseEntity<List<ExperienceResponse>> getUserExperience(@PathVariable UUID userId) {
        return ResponseEntity.ok(experienceService.getByUserId(userId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user experience")
    public ResponseEntity<List<ExperienceResponse>> getMyExperience(Authentication auth) {
        return ResponseEntity.ok(experienceService.getByUserId(currentUserId(auth)));
    }

    @PostMapping
    @Operation(summary = "Add an experience entry")
    public ResponseEntity<ExperienceResponse> addExperience(
            @Valid @RequestBody ExperienceRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(experienceService.create(currentUserId(auth), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an experience entry")
    public ResponseEntity<ExperienceResponse> updateExperience(
            @PathVariable UUID id, @Valid @RequestBody ExperienceRequest request, Authentication auth) {
        return ResponseEntity.ok(experienceService.update(id, currentUserId(auth), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an experience entry")
    public ResponseEntity<Void> deleteExperience(@PathVariable UUID id, Authentication auth) {
        experienceService.delete(id, currentUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
