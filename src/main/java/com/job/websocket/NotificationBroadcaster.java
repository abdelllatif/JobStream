package com.job.websocket;

import com.job.entity.Notification;
import com.job.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationBroadcaster {

    private final NotificationWebSocketHandler webSocketHandler;
    private final NotificationService notificationService;

    @Async
    public void broadcastNotification(Long userId, String title, String message, com.job.enums.NotificationType type) {
        try {
            Notification notification = notificationService.createNotification(userId, title, message, type);
            webSocketHandler.sendNotificationToUser(userId, notification);
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
            webSocketHandler.sendNotificationToUser(userId, notification);
            log.info("Broadcasted job notification to user {}: {}", userId, title);
        } catch (Exception e) {
            log.error("Error broadcasting job notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Async
    public void broadcastMessage(Long userId, String message) {
        try {
            webSocketHandler.sendMessageToUser(userId, message);
            log.info("Broadcasted message to user {}: {}", userId, message);
        } catch (Exception e) {
            log.error("Error broadcasting message to user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Async
    public void broadcastToAll(String message) {
        try {
            webSocketHandler.broadcastToAllUsers(message);
            log.info("Broadcasted message to all users: {}", message);
        } catch (Exception e) {
            log.error("Error broadcasting message to all users: {}", e.getMessage(), e);
        }
    }

    public boolean isUserOnline(Long userId) {
        return webSocketHandler.isUserConnected(userId);
    }

    public int getOnlineUsersCount() {
        return webSocketHandler.getConnectedUsersCount();
    }
}
