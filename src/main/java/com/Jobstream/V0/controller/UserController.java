package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.ChangePasswordRequest;
import com.Jobstream.V0.dto.request.SetPasswordRequest;
import com.Jobstream.V0.dto.request.UpdateRoleRequest;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by email or headline (excludes blocked users)")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String query, Pageable pageable, Authentication authentication) {
        return ResponseEntity.ok(userService.searchUsers(query, authentication.getName(), pageable));
    }

    @GetMapping("/network")
    @Operation(summary = "Get suggested users for network (excluding admins and blocked users)")
    public ResponseEntity<Page<UserResponse>> getNetworkUsers(Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok(userService.getNetworkUsers(authentication.getName(), pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users excluding admins (Admin only)")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsersExcludingAdmins(pageable));
    }

    @GetMapping("/me/has-password")
    @Operation(summary = "Check if current user has a password set")
    public ResponseEntity<Boolean> hasPassword(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.hasPassword(user.getId()));
    }

    @PostMapping("/me/set-password")
    @Operation(summary = "Set password for Google-authenticated users (no password yet)")
    public ResponseEntity<Void> setPassword(
            @Valid @RequestBody SetPasswordRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.setPassword(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/change-password")
    @Operation(summary = "Change current user password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate a suspended user account (Admin only)")
    public ResponseEntity<Void> activateUser(@PathVariable UUID id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Disable a user account (sets enabled=false)")
    public ResponseEntity<Void> disableUser(@PathVariable UUID id, Authentication authentication) {
        userService.disableUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role (Admin only)")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(userService.updateRole(id, request.getRole()));
    }
}
