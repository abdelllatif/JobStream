package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.NotificationResponse;
import com.Jobstream.V0.entity.Notification;

public class NotificationMapper {

    public static NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getType())
                .entityId(notification.getEntityId())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private NotificationMapper() {}
}
