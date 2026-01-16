package com.job.controller;

import com.job.entity.Notification;
import com.job.enums.NotificationType;
import com.job.service.NotificationService;
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

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> createNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type) {
        try {
            Notification notification = notificationService.createNotification(userId, title, message, type);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            log.error("Error creating notification for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create-with-job")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> createNotificationWithJob(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type,
            @RequestParam Long jobId) {
        try {
            Notification notification = notificationService.createNotificationWithJob(userId, title, message, type, jobId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            log.error("Error creating job notification for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/read/{notificationId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking notification {} as read: {}", notificationId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/read-all/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking all notifications as read for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting notification {}: {}", notificationId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable Long userId) {
        try {
            notificationService.deleteAllNotifications(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting all notifications for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/unread-count/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
