package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.SkillRequest;
import com.Jobstream.V0.dto.response.SkillResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.SkillService;
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
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Skills", description = "Endpoints for user skills")
public class SkillController {

    private final SkillService skillService;
    private final UserRepository userRepository;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get skills by user ID")
    public ResponseEntity<List<SkillResponse>> getUserSkills(@PathVariable UUID userId) {
        return ResponseEntity.ok(skillService.getByUserId(userId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user skills")
    public ResponseEntity<List<SkillResponse>> getMySkills(Authentication auth) {
        return ResponseEntity.ok(skillService.getByUserId(getCurrentUserId(auth)));
    }

    @PostMapping
    @Operation(summary = "Add a skill for current user")
    public ResponseEntity<SkillResponse> addSkill(
            @Valid @RequestBody SkillRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(skillService.addSkill(getCurrentUserId(auth), request));
    }

    @DeleteMapping("/{skillId}")
    @Operation(summary = "Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID skillId, Authentication auth) {
        skillService.deleteSkill(skillId, getCurrentUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
