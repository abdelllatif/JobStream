package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.EducationRequest;
import com.Jobstream.V0.dto.response.EducationResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.EducationService;
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
@RequestMapping("/api/educations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Education", description = "Endpoints for user education")
public class EducationController {

    private final EducationService educationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get education by user ID")
    public ResponseEntity<List<EducationResponse>> getUserEducation(@PathVariable UUID userId) {
        return ResponseEntity.ok(educationService.getByUserId(userId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user education")
    public ResponseEntity<List<EducationResponse>> getMyEducation(Authentication auth) {
        return ResponseEntity.ok(educationService.getByUserId(currentUserId(auth)));
    }

    @PostMapping
    @Operation(summary = "Add education entry")
    public ResponseEntity<EducationResponse> addEducation(
            @Valid @RequestBody EducationRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(educationService.create(currentUserId(auth), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an education entry")
    public ResponseEntity<EducationResponse> updateEducation(
            @PathVariable UUID id, @Valid @RequestBody EducationRequest request, Authentication auth) {
        return ResponseEntity.ok(educationService.update(id, currentUserId(auth), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an education entry")
    public ResponseEntity<Void> deleteEducation(@PathVariable UUID id, Authentication auth) {
        educationService.delete(id, currentUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
