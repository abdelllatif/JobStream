package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.ProfileRequest;
import com.Jobstream.V0.dto.response.ProfileResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profiles", description = "Endpoints for user profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get profile by user ID")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getByUserId(userId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ProfileResponse> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(profileService.getByUserId(currentUserId(auth)));
    }

    @PutMapping
    @Operation(summary = "Create or update current user profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody ProfileRequest request, Authentication auth) {
        return ResponseEntity.ok(profileService.createOrUpdate(currentUserId(auth), request));
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile photo")
    public ResponseEntity<ProfileResponse> uploadPhoto(
            @RequestParam("file") MultipartFile file, Authentication auth) {
        return ResponseEntity.ok(profileService.uploadPhoto(currentUserId(auth), file));
    }

    @PostMapping(value = "/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload CV document")
    public ResponseEntity<ProfileResponse> uploadCv(
            @RequestParam("file") MultipartFile file, Authentication auth) {
        return ResponseEntity.ok(profileService.uploadCv(currentUserId(auth), file));
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
