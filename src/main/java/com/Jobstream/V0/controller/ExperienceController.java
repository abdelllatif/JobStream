package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.ExperienceRequest;
import com.Jobstream.V0.dto.response.ExperienceResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.repository.UserRepository;
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
    private final UserRepository userRepository;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get experience by user ID")
    public ResponseEntity<List<ExperienceResponse>> getUserExperience(@PathVariable UUID userId) {
        return ResponseEntity.ok(experienceService.getByUserId(userId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user experience")
    public ResponseEntity<List<ExperienceResponse>> getMyExperience(Authentication auth) {
        return ResponseEntity.ok(experienceService.getByUserId(getCurrentUserId(auth)));
    }

    @PostMapping
    @Operation(summary = "Add an experience entry")
    public ResponseEntity<ExperienceResponse> addExperience(
            @Valid @RequestBody ExperienceRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(experienceService.create(getCurrentUserId(auth), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an experience entry")
    public ResponseEntity<ExperienceResponse> updateExperience(
            @PathVariable UUID id, @Valid @RequestBody ExperienceRequest request, Authentication auth) {
        return ResponseEntity.ok(experienceService.update(id, getCurrentUserId(auth), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an experience entry")
    public ResponseEntity<Void> deleteExperience(@PathVariable UUID id, Authentication auth) {
        experienceService.delete(id, getCurrentUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
