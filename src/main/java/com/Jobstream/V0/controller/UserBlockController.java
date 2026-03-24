package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.UserBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Blocks", description = "Endpoints for blocking/unblocking users")
public class UserBlockController {

    private final UserBlockService userBlockService;
    private final UserRepository userRepository;

    @PostMapping("/{blockedId}")
    @Operation(summary = "Block a user")
    public ResponseEntity<Void> blockUser(@PathVariable UUID blockedId, Authentication auth) {
        userBlockService.blockUser(getCurrentUserId(auth), blockedId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{blockedId}")
    @Operation(summary = "Unblock a user")
    public ResponseEntity<Void> unblockUser(@PathVariable UUID blockedId, Authentication auth) {
        userBlockService.unblockUser(getCurrentUserId(auth), blockedId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @Operation(summary = "Get list of users blocked by current user")
    public ResponseEntity<List<UserResponse>> getBlockedUsers(Authentication auth) {
        return ResponseEntity.ok(userBlockService.getBlockedUsers(getCurrentUserId(auth)));
    }

    private UUID getCurrentUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
