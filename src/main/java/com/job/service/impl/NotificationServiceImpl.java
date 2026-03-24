package com.job.service.impl;

import com.job.entity.Job;
import com.job.entity.Notification;
import com.job.entity.User;
import com.job.enums.NotificationType;
import com.job.repository.JobRepository;
import com.job.repository.NotificationRepository;
import com.job.repository.UserRepository;
import com.job.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Created notification for user {}: {}", userId, title);
        return savedNotification;
    }

    @Override
    @Transactional
    public Notification createNotificationWithJob(Long userId, String title, String message, NotificationType type,
            Long jobId) {
        Notification notification = createNotification(userId, title, message, type);
        if (jobId != null) {
            Job job = jobRepository.findById(jobId).orElse(null);
            notification.setJob(job);
            notificationRepository.save(notification);
        }
        return notification;
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
        log.info("Marked notification {} as read", notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
        log.info("Marked all notifications as read for user {}", userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        log.info("Deleted notification {}", notificationId);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        List<Notification> notifications = getUserNotifications(userId);
        notificationRepository.deleteAll(notifications);
        log.info("Deleted all notifications for user {}", userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }
}
