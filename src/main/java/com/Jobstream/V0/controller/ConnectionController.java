package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.ConnectionRequest;
import com.Jobstream.V0.dto.response.ConnectedUserResponse;
import com.Jobstream.V0.dto.response.ConnectionResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.ConnectionService;
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
@RequestMapping("/api/connections")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Connections", description = "Endpoints for managing user connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping("/request")
    @Operation(summary = "Send a connection request")
    public ResponseEntity<ConnectionResponse> sendRequest(
            @Valid @RequestBody ConnectionRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(connectionService.sendRequest(currentUserId(auth), request));
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Accept a connection request")
    public ResponseEntity<ConnectionResponse> acceptRequest(
            @PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(connectionService.accept(id, currentUserId(auth)));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a connection request")
    public ResponseEntity<ConnectionResponse> rejectRequest(
            @PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(connectionService.reject(id, currentUserId(auth)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove an existing connection or pending request")
    public ResponseEntity<Void> removeConnection(
            @PathVariable UUID id, Authentication auth) {
        connectionService.remove(id, currentUserId(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @Operation(summary = "Get all accepted connections of current user — returns the OTHER person's info")
    public ResponseEntity<List<ConnectedUserResponse>> getMyConnections(Authentication auth) {
        return ResponseEntity.ok(connectionService.getMyConnections(currentUserId(auth)));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending received connection requests")
    public ResponseEntity<List<ConnectionResponse>> getPendingRequests(Authentication auth) {
        return ResponseEntity.ok(connectionService.getPendingRequests(currentUserId(auth)));
    }

    @GetMapping("/sent-pending")
    @Operation(summary = "Get pending sent connection requests")
    public ResponseEntity<List<ConnectionResponse>> getSentPendingRequests(Authentication auth) {
        return ResponseEntity.ok(connectionService.getSentPendingRequests(currentUserId(auth)));
    }

    @GetMapping("/status/{userId}")
    @Operation(summary = "Get connection status with another user")
    public ResponseEntity<ConnectionResponse> getConnectionStatus(
            @PathVariable UUID userId, Authentication auth) {
        ConnectionResponse res = connectionService.getConnectionStatus(currentUserId(auth), userId);
        return res != null ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
