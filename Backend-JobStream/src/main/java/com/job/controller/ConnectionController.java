package com.job.controller;

import com.job.entity.Connection;
import com.job.entity.Connection.ConnectionStatus;
import com.job.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
@Slf4j
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping("/request")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Connection> sendConnectionRequest(
            @RequestParam Long requesterId, 
            @RequestParam Long receiverId) {
        try {
            Connection connection = connectionService.sendConnectionRequest(requesterId, receiverId);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            log.error("Error sending connection request from {} to {}: {}", requesterId, receiverId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/accept/{connectionId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Connection> acceptConnectionRequest(@PathVariable Long connectionId) {
        try {
            Connection connection = connectionService.acceptConnectionRequest(connectionId);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            log.error("Error accepting connection request {}: {}", connectionId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/reject/{connectionId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Connection> rejectConnectionRequest(@PathVariable Long connectionId) {
        try {
            Connection connection = connectionService.rejectConnectionRequest(connectionId);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            log.error("Error rejecting connection request {}: {}", connectionId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/block")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Void> blockUser(
            @RequestParam Long blockerId, 
            @RequestParam Long blockedId) {
        try {
            connectionService.blockUser(blockerId, blockedId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error blocking user {} by user {}: {}", blockedId, blockerId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/unblock")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Void> unblockUser(
            @RequestParam Long blockerId, 
            @RequestParam Long blockedId) {
        try {
            connectionService.unblockUser(blockerId, blockedId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error unblocking user {} by user {}: {}", blockedId, blockerId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Connection>> getUserConnections(@PathVariable Long userId) {
        List<Connection> connections = connectionService.getUserConnections(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/pending/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<List<Connection>> getPendingRequests(@PathVariable Long userId) {
        List<Connection> connections = connectionService.getPendingRequests(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/sent/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<List<Connection>> getSentRequests(@PathVariable Long userId) {
        List<Connection> connections = connectionService.getSentRequests(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/check")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkConnection(
            @RequestParam Long userId1, 
            @RequestParam Long userId2) {
        boolean areConnected = connectionService.areUsersConnected(userId1, userId2);
        return ResponseEntity.ok(Map.of("connected", areConnected));
    }

    @GetMapping("/between/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Connection> getConnectionBetweenUsers(
            @PathVariable Long userId1, 
            @PathVariable Long userId2) {
        return connectionService.getConnectionBetweenUsers(userId1, userId2)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
