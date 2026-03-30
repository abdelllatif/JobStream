package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.response.NotificationCountResponse;
import com.Jobstream.V0.dto.response.NotificationResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "Endpoints for real-time notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    @Operation(summary = "Get my notifications")
    public ResponseEntity<PageResponse<NotificationResponse>> getMyNotifications(
            Authentication auth, Pageable pageable) {
        return ResponseEntity.ok(notificationService.getMyNotifications(currentUserId(auth), pageable));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get total unread notification count")
    public ResponseEntity<Long> getUnreadCount(Authentication auth) {
        return ResponseEntity.ok(notificationService.countUnread(currentUserId(auth)));
    }

    @GetMapping("/unread-counts")
    @Operation(summary = "Get split unread counts: notificationCount (non-message) and messageCount")
    public ResponseEntity<NotificationCountResponse> getUnreadCounts(Authentication auth) {
        return ResponseEntity.ok(notificationService.getUnreadCounts(currentUserId(auth)));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(notificationService.markAsRead(id, currentUserId(auth)));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Integer> markAllAsRead(Authentication auth) {
        return ResponseEntity.ok(notificationService.markAllAsRead(currentUserId(auth)));
    }

    @PutMapping("/read-messages")
    @Operation(summary = "Mark all MESSAGE-type notifications as read (call when opening Messages panel)")
    public ResponseEntity<Integer> markMessageNotificationsAsRead(Authentication auth) {
        return ResponseEntity.ok(notificationService.markMessageNotificationsAsRead(currentUserId(auth)));
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
