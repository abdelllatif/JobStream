package com.job.websocket;

import com.job.dto.response.NotificationDTO;
import com.job.entity.Notification;
import com.job.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationBroadcaster {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

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

    private String mapTypeToIcon(com.job.enums.NotificationType type) {
        if (type == null) return "🔔";
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

    @Async
    public void broadcastNotification(Long userId, String title, String message, com.job.enums.NotificationType type) {
        try {
            Notification notification = notificationService.createNotification(userId, title, message, type);
            messagingTemplate.convertAndSend("/topic/notifications." + userId, toDto(notification));
            log.info("Broadcasted notification to user {}: {}", userId, title);
        } catch (Exception e) {
            log.error("Error broadcasting notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Async
    public void broadcastNotificationWithJob(Long userId, String title, String message, 
                                            com.job.enums.NotificationType type, Long jobId) {
        try {
            Notification notification = notificationService.createNotificationWithJob(userId, title, message, type, jobId);
            messagingTemplate.convertAndSend("/topic/notifications." + userId, toDto(notification));
            log.info("Broadcasted job notification to user {}: {}", userId, title);
        } catch (Exception e) {
            log.error("Error broadcasting job notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Async
    public void broadcastMessage(Long userId, String message) {
        try {
            log.info("Broadcasted message to user {}: {}", userId, message);
        } catch (Exception e) {
            log.error("Error broadcasting message to user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Async
    public void broadcastToAll(String message) {
        try {
            log.info("Broadcasted message to all users: {}", message);
        } catch (Exception e) {
            log.error("Error broadcasting message to all users: {}", e.getMessage(), e);
        }
    }

    public boolean isUserOnline(Long userId) {
        return false;
    }

    public int getOnlineUsersCount() {
        return 0;
    }
}
