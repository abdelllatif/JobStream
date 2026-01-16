package com.job.service;

import com.job.entity.Notification;
import com.job.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Long userId, String title, String message, NotificationType type);
    Notification createNotificationWithJob(Long userId, String title, String message, NotificationType type, Long jobId);
    List<Notification> getUserNotifications(Long userId);
    List<Notification> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId);
    void deleteAllNotifications(Long userId);
    long getUnreadCount(Long userId);
}
