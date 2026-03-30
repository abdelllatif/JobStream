package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.Notification;
import com.Jobstream.V0.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /** Total unread (all types). */
    long countByUserIdAndIsReadFalse(UUID userId);

    /** Unread count for a specific type (e.g. MESSAGE). */
    long countByUserIdAndIsReadFalseAndType(UUID userId, NotificationType type);

    /** Unread count for every type EXCEPT the given one (e.g. excluding MESSAGE). */
    long countByUserIdAndIsReadFalseAndTypeNot(UUID userId, NotificationType type);

    /** Mark all unread notifications as read, REGARDLESS of type. */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") UUID userId);

    /**
     * Mark all unread notifications as read, EXCLUDING the given type.
     * Used by the notification-bell "mark all as read" action so that
     * MESSAGE-type notifications are left untouched.
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
           "WHERE n.user.id = :userId AND n.type != :type AND n.isRead = false")
    int markAllAsReadExcludingType(@Param("userId") UUID userId,
                                   @Param("type") NotificationType type);

    /** Mark all unread notifications of a specific type as read. */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
           "WHERE n.user.id = :userId AND n.type = :type AND n.isRead = false")
    int markAllAsReadByType(@Param("userId") UUID userId,
                            @Param("type") NotificationType type);
}
