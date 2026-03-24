package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.response.NotificationResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.Notification;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.NotificationType;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.NotificationMapper;
import com.Jobstream.V0.repository.NotificationRepository;
import com.Jobstream.V0.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public Notification createNotification(User recipient, NotificationType type, UUID entityId, String content) {
        Notification notification = Notification.builder()
                .user(recipient)
                .type(type)
                .entityId(entityId)
                .content(content)
                .isRead(false)
                .build();
        
        notification = notificationRepository.save(notification);

        // Real-time broadcast
        NotificationResponse response = NotificationMapper.toResponse(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + recipient.getId(), response);

        return notification;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.<NotificationResponse>builder()
                .content(page.getContent().stream().map(NotificationMapper::toResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .last(page.isLast()).build();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not your notification");
        }
        
        notification.setRead(true);
        return NotificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public int markAllAsRead(UUID userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}
