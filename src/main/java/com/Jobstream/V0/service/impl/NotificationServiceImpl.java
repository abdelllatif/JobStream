package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.response.NotificationCountResponse;
import com.Jobstream.V0.dto.response.NotificationResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.Notification;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.NotificationType;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.NotificationMapper;
import com.Jobstream.V0.repository.NotificationRepository;
import com.Jobstream.V0.repository.UserRepository;
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
    private final UserRepository userRepository;
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

        NotificationResponse response = NotificationMapper.toResponse(notification);
        messagingTemplate.convertAndSendToUser(recipient.getEmail(), "/queue/notifications", response);
        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(), "/queue/notifications/count",
                buildCounts(recipient.getId()));

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
        NotificationResponse response = NotificationMapper.toResponse(notificationRepository.save(notification));
        messagingTemplate.convertAndSendToUser(
                notification.getUser().getEmail(), "/queue/notifications/count",
                buildCounts(userId));
        return response;
    }

    @Override
    @Transactional
    public int markAllAsRead(UUID userId) {
        int updated = notificationRepository.markAllAsReadExcludingType(userId, NotificationType.MESSAGE);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        long msgCount = notificationRepository.countByUserIdAndIsReadFalseAndType(userId, NotificationType.MESSAGE);
        messagingTemplate.convertAndSendToUser(
                user.getEmail(), "/queue/notifications/count",
                new NotificationCountResponse(0L, msgCount));
        return updated;
    }

    @Override
    @Transactional
    public int markMessageNotificationsAsRead(UUID userId) {
        int updated = notificationRepository.markAllAsReadByType(userId, NotificationType.MESSAGE);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        long notifCount = notificationRepository.countByUserIdAndIsReadFalseAndTypeNot(userId, NotificationType.MESSAGE);
        messagingTemplate.convertAndSendToUser(
                user.getEmail(), "/queue/notifications/count",
                new NotificationCountResponse(notifCount, 0L));
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationCountResponse getUnreadCounts(UUID userId) {
        return buildCounts(userId);
    }


    private NotificationCountResponse buildCounts(UUID userId) {
        long notifCount = notificationRepository
                .countByUserIdAndIsReadFalseAndTypeNot(userId, NotificationType.MESSAGE);
        long msgCount = notificationRepository
                .countByUserIdAndIsReadFalseAndType(userId, NotificationType.MESSAGE);
        return new NotificationCountResponse(notifCount, msgCount);
    }
}
