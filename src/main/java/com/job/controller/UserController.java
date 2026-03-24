package com.job.controller;

import com.job.dto.request.UserUpdateRequest;
import com.job.dto.response.UserResponseDTO;
import com.job.service.UserService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AuthUtil authUtil;

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UserUpdateRequest request) {
        Long currentUserId = authUtil.getCurrentUserId();
        UserResponseDTO updatedUser = userService.updateUser(currentUserId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<java.util.List<com.job.dto.response.UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
