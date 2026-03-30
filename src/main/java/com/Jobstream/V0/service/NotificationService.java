package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.response.NotificationCountResponse;
import com.Jobstream.V0.dto.response.NotificationResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.Notification;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.NotificationType;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    Notification createNotification(User recipient, NotificationType type,
                                     UUID entityId, String content);

    PageResponse<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable);

    NotificationResponse markAsRead(UUID notificationId, UUID userId);

    /** Mark all non-MESSAGE notifications as read (notification-bell action). */
    int markAllAsRead(UUID userId);

    /** Mark all MESSAGE notifications as read (messages-panel action). */
    int markMessageNotificationsAsRead(UUID userId);

    long countUnread(UUID userId);

    /** Returns split unread counts: messageCount and notificationCount. */
    NotificationCountResponse getUnreadCounts(UUID userId);
}
