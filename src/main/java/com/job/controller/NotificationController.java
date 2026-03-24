package com.job.controller;

import com.job.dto.response.NotificationDTO;
import com.job.entity.Notification;
import com.job.enums.NotificationType;
import com.job.service.NotificationService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthUtil authUtil;

    private NotificationDTO toDto(Notification notification) {
        String icon = mapTypeToIcon(notification.getType());
        return NotificationDTO.builder()
                .id(notification.getId())
                .icon(icon)
                .title(notification.getTitle())
                .body(notification.getMessage())
                .time(notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null)
                .read(notification.isRead())
                .type(notification.getType())
                .jobId(notification.getJob() != null ? notification.getJob().getId() : null)
                .candidateProfileId(notification.getCandidateProfile() != null ? notification.getCandidateProfile().getId() : null)
                .build();
    }

    private String mapTypeToIcon(NotificationType type) {
        if (type == null) {
            return "🔔";
        }
        return switch (type) {
            case APPLICATION_RECEIVED -> "📩";
            case APPLICATION_STATUS_CHANGED -> "✅";
            case JOB_RECOMMENDATION -> "💼";
            case PROFILE_VISIT -> "👀";
            case CONNECTION_REQUEST -> "🤝";
            case MESSAGE_RECEIVED -> "💬";
            case JOB_EXPIRED -> "⏰";
            case PREMIUM_EXPIRING -> "⭐";
            case PAYMENT_SUCCESSFUL -> "💰";
            case PAYMENT_FAILED -> "⚠️";
        };
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> createNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type) {
        Notification notification = notificationService.createNotification(userId, title, message, type);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/create-with-job")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> createNotificationWithJob(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type,
            @RequestParam Long jobId) {
        Notification notification = notificationService.createNotificationWithJob(userId, title, message, type, jobId);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/read/{notificationId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Frontend-friendly list endpoint:
     * GET /api/notifications?userId=
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByQuery(@RequestParam("userId") Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        List<NotificationDTO> dtoList = notifications.stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Frontend-friendly mark-read endpoints using POST semantics.
     */
    @PostMapping("/mark-read/{notificationId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAsReadPost(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAllAsReadPost(@RequestParam("userId") Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
