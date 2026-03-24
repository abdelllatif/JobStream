package com.job.controller;

import com.job.entity.Connection;
import com.job.service.ConnectionService;
import com.job.util.AuthUtil;
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
    private final AuthUtil authUtil;

    @PostMapping("/request")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Connection> sendConnectionRequest(
            @RequestParam Long receiverId) {
        try {
            Long requesterId = authUtil.getCurrentUserId();
            Connection connection = connectionService.sendConnectionRequest(requesterId, receiverId);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            log.error("Error sending connection request from current user to {}: {}", receiverId, e.getMessage());
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
            @RequestParam Long blockedId) {
        try {
            Long blockerId = authUtil.getCurrentUserId();
            connectionService.blockUser(blockerId, blockedId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error blocking user {} by current user: {}", blockedId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/unblock")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Void> unblockUser(
            @RequestParam Long blockedId) {
        try {
            Long blockerId = authUtil.getCurrentUserId();
            connectionService.unblockUser(blockerId, blockedId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error unblocking user {} by current user: {}", blockedId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Connection>> getUserConnections(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Connection> connections = connectionService.getUserConnections(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/pending/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<List<Connection>> getPendingRequests(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Connection> connections = connectionService.getPendingRequests(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/sent/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<List<Connection>> getSentRequests(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Connection> connections = connectionService.getSentRequests(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/check")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkConnection(
            @RequestParam Long otherUserId) {
        Long currentUserId = authUtil.getCurrentUserId();
        boolean areConnected = connectionService.areUsersConnected(currentUserId, otherUserId);
        return ResponseEntity.ok(Map.of("connected", areConnected));
    }

    @GetMapping("/between/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Connection> getConnectionBetweenUsers(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId1) && !currentUserId.equals(userId2)) {
            return ResponseEntity.badRequest().build();
        }
        return connectionService.getConnectionBetweenUsers(userId1, userId2)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/network")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<com.job.dto.response.UserNetworkResponseDTO>> getNetworkUsers() {
        Long currentUserId = authUtil.getCurrentUserId();
        List<com.job.dto.response.UserNetworkResponseDTO> networkUsers = connectionService
                .getNetworkUsers(currentUserId);
        return ResponseEntity.ok(networkUsers);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<com.job.dto.response.UserNetworkResponseDTO>> searchNetworkUsers(
            @RequestParam String query) {
        Long currentUserId = authUtil.getCurrentUserId();
        List<com.job.dto.response.UserNetworkResponseDTO> networkUsers = connectionService
                .getNetworkUsers(currentUserId);

        String lowerQuery = query.toLowerCase();
        List<com.job.dto.response.UserNetworkResponseDTO> filtered = networkUsers.stream()
                .filter(u -> (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(lowerQuery)) ||
                        (u.getLastName() != null && u.getLastName().toLowerCase().contains(lowerQuery)) ||
                        (u.getJobTitle() != null && u.getJobTitle().toLowerCase().contains(lowerQuery)))
                .toList();

        return ResponseEntity.ok(filtered);
    }

    @PutMapping("/accept/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Connection> acceptConnectionRequestByUser(@PathVariable Long userId) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();
            Connection connection = connectionService.getPendingRequests(currentUserId).stream()
                    .filter(c -> c.getRequester().getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Pending connection request not found"));

            Connection acceptedConnection = connectionService.acceptConnectionRequest(connection.getId());
            return ResponseEntity.ok(acceptedConnection);
        } catch (Exception e) {
            log.error("Error accepting connection request from user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/unconnect/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Void> deleteConnection(@PathVariable Long userId) {
        try {
            Long currentUserId = authUtil.getCurrentUserId();
            connectionService.deleteConnection(currentUserId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting connection between current user and user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<com.job.dto.response.ConnectionListResponseDTO> getConnectionList() {
        Long currentUserId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(connectionService.getConnectionListForUser(currentUserId));
    }
}
